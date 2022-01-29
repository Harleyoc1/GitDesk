package com.harleyoconnor.gitdesk.ui.repository.changes

import com.harleyoconnor.gitdesk.git.repository.Repository
import com.harleyoconnor.gitdesk.ui.UIResource
import com.harleyoconnor.gitdesk.ui.node.SVGIcon
import com.harleyoconnor.gitdesk.ui.style.SELECTED_PSEUDO_CLASS
import com.harleyoconnor.gitdesk.ui.util.getIcon
import com.harleyoconnor.gitdesk.ui.util.setOnActions
import com.harleyoconnor.gitdesk.ui.view.ResourceViewLoader
import com.harleyoconnor.gitdesk.ui.view.ViewController
import com.harleyoconnor.gitdesk.util.getUserHome
import com.harleyoconnor.gitdesk.util.process.logFailure
import com.harleyoconnor.gitdesk.util.system.SystemManager
import javafx.event.ActionEvent
import javafx.event.Event
import javafx.fxml.FXML
import javafx.scene.control.CheckBox
import javafx.scene.control.ContextMenu
import javafx.scene.control.Label
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import javafx.scene.layout.HBox
import org.apache.logging.log4j.LogManager

/**
 * @author Harley O'Connor
 */
class FileCellController : ViewController<FileCellController.Context> {

    object Loader : ResourceViewLoader<Context, FileCellController, HBox>(
        UIResource("/ui/layouts/repository/changes/FileCell.fxml")
    )

    class Context(val repository: Repository, val file: Repository.ChangedFile, val listener: ChangedFileListener) :
        ViewController.Context

    private lateinit var repository: Repository
    private lateinit var file: Repository.ChangedFile
    private lateinit var listener: ChangedFileListener

    @FXML
    private lateinit var contextMenu: ContextMenu

    @FXML
    private lateinit var cell: HBox

    @FXML
    private lateinit var stageCheckbox: CheckBox

    @FXML
    private lateinit var icon: SVGIcon

    @FXML
    private lateinit var nameLabel: Label

    override fun setup(context: Context) {
        this.repository = context.repository
        this.file = context.file
        this.listener = context.listener

        this.stageCheckbox.isSelected = file.staged
        this.stageCheckbox.setOnActions(this::addToStage, this::removeFromStage)
        this.icon.setupFromSvg(file.file.getIcon())
        this.nameLabel.text = file.file.name
        this.nameLabel.tooltip.text = file.file.absolutePath.replace(getUserHome(), "~")
    }

    private fun addToStage() {
        repository.addToStage(file.file)
            .ifSuccessful {
                listener.onFileStaged()
            }
            .ifFailure {
                LogManager.getLogger().error("Failed to stage file `${file.file}` with error `${it.error}`.")
            }
            .begin()
    }

    private fun removeFromStage() {
        repository.removeFromStage(file.file)
            .ifSuccessful {
                listener.onFileUnStaged()
            }
            .ifFailure {
                LogManager.getLogger()
                    .error("Failed to remove file `${file.file}` from stage with error `${it.error}`.")
            }
            .begin()
    }

    @FXML
    private fun select(event: MouseEvent) {
        if (event.button == MouseButton.SECONDARY || event.isControlDown) {
            contextMenu.show(cell, event.screenX, event.screenY)
        } else if (cell.pseudoClassStates.contains(SELECTED_PSEUDO_CLASS)) {
            open(event)
            return
        }
        cell.pseudoClassStateChanged(SELECTED_PSEUDO_CLASS, true)
    }

    fun deselect() {
        cell.pseudoClassStateChanged(SELECTED_PSEUDO_CLASS, false)
    }

    @FXML
    private fun open(event: Event) {

    }

    @FXML
    private fun openInEditor(event: ActionEvent) {

    }

    @FXML
    private fun openInFileBrowser(event: ActionEvent) {
        SystemManager.get().openInFileBrowser(file.file).begin()
    }

    @FXML
    private fun rollback(event: ActionEvent) {
        repository.rollback(file.file)
            .ifSuccessful {
                listener.onFileReset()
            }
            .ifFailure(::logFailure)
            .begin()
    }


}