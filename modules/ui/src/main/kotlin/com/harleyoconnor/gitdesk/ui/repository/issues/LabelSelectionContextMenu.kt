package com.harleyoconnor.gitdesk.ui.repository.issues

import com.harleyoconnor.gitdesk.data.remote.Label
import com.harleyoconnor.gitdesk.ui.repository.RemoteContext
import com.harleyoconnor.gitdesk.util.stream
import com.harleyoconnor.gitdesk.util.toHexColourString
import javafx.scene.control.ContextMenu
import javafx.scene.control.MenuItem

/**
 * @author Harley O'Connor
 */
class LabelSelectionContextMenu(
    private val remoteContext: RemoteContext,
    private val issue: IssueAccessor,
    private val callback: (Label) -> Unit
) : ContextMenu() {

    init {
        loadLabels()
    }

    private fun loadLabels() {
        remoteContext.remote.labels.stream()
            .filter { label ->
                notAlreadyAdded(label)
            }
            .map(this::createLabelMenuItem)
            .forEach(this.items::add)
    }

    private fun notAlreadyAdded(label: Label) =
        issue.get().labels.stream().noneMatch { it.name == label.name }

    private fun createLabelMenuItem(
        label: Label
    ): MenuItem {
        return MenuItem(label.name).also {
            it.setOnAction {
                callback(label)
            }
            it.style = "-fx-border-color: ${label.colour.toHexColourString()};" +
                    "-fx-background-radius: 0.6667em;" +
                    "-fx-border-radius: 0.6667em;" +
                    "-fx-border-width: 2px;" +
                    "-fx-padding: 0.16667em 0.3334em;"
        }
    }

}