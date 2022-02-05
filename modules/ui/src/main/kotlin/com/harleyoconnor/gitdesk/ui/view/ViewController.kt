package com.harleyoconnor.gitdesk.ui.view

/**
 * An FXML view controller.
 *
 * @author Harley O'Connor
 */
interface ViewController<C : ViewController.Context> {

    /**
     * Performs additional content using passed context.
     *
     * @param context the context object, containing parameters required for setup
     */
    fun setup(context: C)

    /**
     * An object holding context required to set up this view. Effectively holds its parameters.
     */
    interface Context

}