/*
 * Copyright (c) 2020 Razeware LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * Notwithstanding the foregoing, you may not use, copy, modify, merge, publish,
 * distribute, sublicense, create a derivative work, and/or sell copies of the
 * Software in any work that is designed, intended, or marketed for pedagogical or
 * instructional purposes related to programming, coding, application development,
 * or information technology.  Permission for such use, copying, modification,
 * merger, publication, distribution, sublicensing, creation of derivative works,
 * or sale is expressly withheld.
 *
 * This project and source code may use libraries or frameworks that are
 * released under various Open-Source licenses. Use of those libraries and
 * frameworks are governed by their own individual licenses.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.raywenderlich.android.raydi.processor

import com.google.auto.service.AutoService
import com.raywenderlich.android.raydi.annotations.RayBind
import com.raywenderlich.android.raydi.annotations.RayDi
import com.raywenderlich.android.raydi.annotations.RayInject
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import java.io.File
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement
import javax.lang.model.type.MirroredTypeException
import javax.tools.Diagnostic.Kind
import kotlin.reflect.KClass

@AutoService(Processor::class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedOptions(RayDaggerProcessor.KAPT_KOTLIN_GENERATED_OPTION_NAME)
class RayDaggerProcessor : AbstractProcessor() {

  companion object {
    const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
  }

  override fun process(
      annotations: MutableSet<out TypeElement>,
      roundEnv: RoundEnvironment
  ): Boolean {
    val casesOptions = mutableMapOf<ClassName, ClassName>()
    roundEnv.getElementsAnnotatedWith(RayDi::class.java).forEach { element ->
      createObjectFactories(element, casesOptions)
    }
    roundEnv.getElementsAnnotatedWith(RayBind::class.java).forEach { element ->
      createObjectFactoriesForBind(element, casesOptions)
    }
    createMainFactory(roundEnv, casesOptions)
    return false
  }

  fun createObjectFactories(element: Element, cases: MutableMap<ClassName, ClassName>) {
    val generatedSourcesRoot: String =
        processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME].orEmpty()
    val file = File(generatedSourcesRoot)
        .apply {
          mkdir()
        }
    val packageForFactory = processingEnv.elementUtils.getPackageOf(element).toString()
    val targetClassName = (element as TypeElement).simpleName.toString()
    val objectFactoryClassName = "${targetClassName}_RayDiFactory"
    val targetClass = ClassName(packageForFactory, targetClassName)
    val objectFactoryInterfaceName = ClassName(
        "com.raywenderlich.android.raydi",
        "RayDiObjectFactory"
    ).parameterizedBy(targetClass)
    cases[targetClass] = ClassName(packageForFactory, objectFactoryClassName)
    FileSpec.builder(packageForFactory, objectFactoryClassName)
        .addType(
            TypeSpec.classBuilder(objectFactoryClassName)
                .addSuperinterface(objectFactoryInterfaceName)
                .addFunction(
                    FunSpec.builder("create")
                        .returns(targetClass)
                        .addStatement("return %T()", targetClass)
                        .addModifiers(KModifier.OVERRIDE)
                        .build()
                )
                .build()
        )
        .build()
        .writeTo(file)
  }

  fun createObjectFactoriesForBind(element: Element, cases: MutableMap<ClassName, ClassName>) {
    val generatedSourcesRoot: String =
        processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME].orEmpty()
    val file = File(generatedSourcesRoot)
        .apply {
          mkdir()
        }
    val packageForFactory = processingEnv.elementUtils.getPackageOf(element).toString()
    val targetKClass = element.getAnnotationClassValue<RayBind> {
      interfaceClazz
    }
    val nameForType = processingEnv.typeUtils.asElement(targetKClass) as TypeElement
    val interfaceClassName = nameForType.simpleName.toString()
    val targetClassName = (element as TypeElement).simpleName.toString()
    val objectFactoryClassName = "${interfaceClassName}_RayDiFactory"
    val targetClass = ClassName(packageForFactory, nameForType.simpleName.toString())
    val actualTargetClass = ClassName(packageForFactory, targetClassName)
    val ObjectFactoryInterfaceName = ClassName(
        "com.raywenderlich.android.raydi",
        "RayDiObjectFactory"
    ).parameterizedBy(targetClass)
    cases[targetClass] = ClassName(packageForFactory, objectFactoryClassName)
    FileSpec.builder(packageForFactory, objectFactoryClassName)
        .addType(
            TypeSpec.classBuilder(objectFactoryClassName)
                .addSuperinterface(ObjectFactoryInterfaceName)
                .addFunction(
                    FunSpec.builder("create")
                        .returns(targetClass)
                        .addStatement("return %T()", actualTargetClass)
                        .addModifiers(KModifier.OVERRIDE)
                        .build()
                )
                .build()
        )
        .build()
        .writeTo(file)
  }

  data class InjectInfo(
      val propertyName: String, // The name of the property
      val targetClassName: ClassName // The classname for the factory to invoke
  )

  fun createMainFactory(roundEnv: RoundEnvironment, cases: MutableMap<ClassName, ClassName>) {
    if (cases.isEmpty()) {
      return
    }
    processingEnv.messager.noteMessage { "CASES : ${cases} " }

    val generatedSourcesRoot: String =
        processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME].orEmpty()
    val file = File(generatedSourcesRoot)
    file.mkdir()
    val RayDiFactoryName = ClassName(
        "raydi", "RayDiFactory"
    )
    val funcBuilder = FunSpec.builder("get")
        .addTypeVariable(TypeVariableName("T"))
        .returns(TypeVariableName("T"))
        .addAnnotation(
            AnnotationSpec.builder(
                ClassName(
                    "kotlin", "Suppress"
                )
            ).addMember("\"UNCHECKED_CAST\"")
                .build()
        )
        .addParameter(
            ParameterSpec.builder(
                "type",
                KClass::class.asClassName().parameterizedBy(WildcardTypeName.producerOf(ANY))
            ).build()
        ).beginControlFlow("val target = when(type)")

    // We search for the property to inject in the different classes
    val propertyToInjectMap = mutableMapOf<ClassName, MutableList<InjectInfo>>()
    roundEnv.getElementsAnnotatedWith(RayInject::class.java).forEach { element ->
      // The target of the injection
      val enclosingClassPackage =
          processingEnv.elementUtils.getPackageOf(element.enclosingElement).toString()
      val enclosingClassSimpleName = (element.enclosingElement as TypeElement).simpleName.toString()
      val enclosingClassName = ClassName(enclosingClassPackage, enclosingClassSimpleName)
      // Information about the field to inject
      val variableName = (element as VariableElement).simpleName.toString()
      val variableClassName = ClassName.bestGuess(element.asType().toString())
      val injectInfo = InjectInfo(variableName, variableClassName)
      val existingInjection = propertyToInjectMap[enclosingClassName]
      if (existingInjection == null) {
        propertyToInjectMap[enclosingClassName] = mutableListOf(injectInfo)
      } else {
        existingInjection.add(injectInfo)
      }
    }

    cases.keys.forEach { typeCase ->
      funcBuilder.addCode(
          injectionBlock(
              typeCase,
              cases,
              propertyToInjectMap[typeCase]
          )
      )
    }
    funcBuilder
        .addStatement("else -> throw IllegalStateException()")
        .endControlFlow()
        .addStatement("return target as T")
    FileSpec.builder("raydi", "RayDiFactory")
        .addType(
            TypeSpec.classBuilder(RayDiFactoryName)
                .addFunction(
                    funcBuilder.build()
                )
                .build()
        )
        .build()
        .writeTo(file)
  }

  fun injectionBlock(
      key: ClassName,
      cases: MutableMap<ClassName, ClassName>,
      injectionList: MutableList<InjectInfo>?
  ): CodeBlock {
    val blockBuilder = CodeBlock.builder()
        .addStatement("%T::class -> %T().create()", key, cases[key]!!)
    if (!injectionList.isNullOrEmpty()) {
      blockBuilder.addStatement(".apply {")
      injectionList.forEach {
        blockBuilder.addStatement("${it.propertyName} = %T().create()", cases[it.targetClassName]!!)
      }
      blockBuilder.addStatement("}")
    }
    blockBuilder.addStatement("as T")
    return blockBuilder.build()
  }


  inline fun <reified T : Annotation> Element.getAnnotationClassValue(f: T.() -> KClass<*>) = try {
    getAnnotation(T::class.java).f()
    throw Exception("Expected to get a MirroredTypeException")
  } catch (e: MirroredTypeException) {
    e.typeMirror
  }

  override fun getSupportedAnnotationTypes(): MutableSet<String> {
    return mutableSetOf(
        RayInject::class.java.canonicalName,
        RayBind::class.java.canonicalName,
        RayDi::class.java.canonicalName
    )
  }

  override fun getSupportedSourceVersion(): SourceVersion? {
    return SourceVersion.latestSupported()
  }


  fun Messager.errorMessage(fn: () -> String) {
    printMessage(Kind.ERROR, fn())
  }

  fun Messager.noteMessage(fn: () -> String) {
    printMessage(Kind.NOTE, fn())
  }

  fun Messager.warningMessage(fn: () -> String) {
    printMessage(Kind.WARNING, fn())
  }
}

