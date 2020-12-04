package com.raywenderlich.android.mvp

/**
 * A Presenter in the MVP architecture. It's bound to a specific View
 */
interface Presenter<V, VB : ViewBinder<V>> {

  /**
   * Binds the view to the Presenter
   */
  fun bind(viewBinder: VB)

  /**
   * Unbinds the View
   */
  fun unbind()
}