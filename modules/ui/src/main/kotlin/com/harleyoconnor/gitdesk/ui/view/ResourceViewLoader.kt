package com.harleyoconnor.gitdesk.ui.view

import com.harleyoconnor.gitdesk.ui.translation.TRANSLATIONS_BUNDLE
import com.harleyoconnor.gitdesk.util.Resource
import javafx.fxml.FXMLLoader
import java.util.ResourceBundle

/**
 * A view loader that loads from a `resources` directory resource.
 *
 * @property resource the FXML resource file
 * @property resources the resources used to resolve resource key attribute values
 * @author Harley O'Connor
 */
open class ResourceViewLoader<C : ViewController.Context, V : ViewController<C>, R>(
    private val resource: Resource,
    private val resources: ResourceBundle = TRANSLATIONS_BUNDLE
) : ViewLoader<C, V, R> {

    override fun load(context: C): ViewLoader.View<V, R> {
        val fxmlLoader = FXMLLoader(resource.location, resources)
        val root = fxmlLoader.load<R>()
        val controller = fxmlLoader.getController<V>()
        controller.setup(context)
        return ViewLoader.View(root, controller)
    }
}