package com.harleyoconnor.gitdesk.ui.repository.issues

import com.harleyoconnor.gitdesk.data.remote.Label
import com.harleyoconnor.gitdesk.ui.UIResource
import com.harleyoconnor.gitdesk.ui.view.ResourceViewLoader
import com.harleyoconnor.gitdesk.ui.view.ViewController
import com.harleyoconnor.gitdesk.util.toHexColourString
import javafx.fxml.FXML
import javafx.scene.control.Tooltip
import javafx.scene.layout.Pane

/**
 * @author Harley O'Connor
 */
class LabelController : ViewController<LabelController.Context> {

    object Loader: ResourceViewLoader<Context, LabelController, Pane>(
        UIResource("/ui/layouts/repository/issues/Label.fxml")
    )

    class Context(val label: Label): ViewController.Context

    @FXML
    private lateinit var root: Pane

    @FXML
    private lateinit var nameLabel: javafx.scene.control.Label

    override fun setup(context: Context) {
        nameLabel.text = context.label.name
        context.label.description?.let {
            nameLabel.tooltip = Tooltip(it)
        }
        root.style += "-fx-border-color: ${context.label.colour.toHexColourString()};"
    }
}