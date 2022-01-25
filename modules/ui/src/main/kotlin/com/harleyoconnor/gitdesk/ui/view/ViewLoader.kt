package com.harleyoconnor.gitdesk.ui.view

/**
 * Handles loading an FXML view.
 *
 * @author Harley O'Connor
 */
interface ViewLoader<C : ViewController.Context, V : ViewController<C>, R> {

    /**
     * Loads the FXML view handled by this loader.
     *
     * @param context the context object to pass to the controller
     * @return the loaded view, in terms of its root node and controller
     */
    fun load(context: C): View<V, R>

    /**
     * A loaded FXML view, in terms of the [root] node and [controller].
     */
    data class View<C : ViewController<*>, R>(
        val root: R,
        val controller: C
    )

}