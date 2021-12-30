package com.harleyoconnor.gitdesk.ui.repository.changes

import com.harleyoconnor.gitdesk.ui.node.SVGIcon
import com.harleyoconnor.gitdesk.ui.util.getIcon
import com.harleyoconnor.gitdesk.ui.util.load
import com.harleyoconnor.gitdesk.ui.util.setOnActions
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.CheckBox
import javafx.scene.control.Label
import javafx.scene.input.MouseEvent
import javafx.scene.layout.HBox
import org.apache.logging.log4j.LogManager
import java.io.File

/**
 * @author Harley O'Connor
 */
class FileCellController {

    companion object {
        fun load(file: File, parent: ChangedFileListController): HBox {
            val fxml = load<HBox, FileCellController>("repository/changes/FileCell")
            fxml.controller.setup(file, parent)
            return fxml.root
        }
    }

    private lateinit var file: File

    private lateinit var parent: ChangedFileListController

    @FXML
    private lateinit var cell: HBox

    @FXML
    private lateinit var stageCheckbox: CheckBox
    @FXML
    private lateinit var icon: SVGIcon
    @FXML
    private lateinit var nameLabel: Label

    private fun setup(file: File, parent: ChangedFileListController) {
        this.file = file
        this.parent = parent

        this.icon.setupFromSvg(file.getIcon())
        this.nameLabel.text = file.name
        this.stageCheckbox.setOnActions(this::addToStage, this::removeFromStage)
    }

    private fun addToStage() {
        parent.repository.gitRepository.addToStage(file)
            .ifFailure {
                LogManager.getLogger().error("Failed to stage file `$file` with error `${it.error}`.")
            }
            .begin()
    }

    private fun removeFromStage() {
        parent.repository.gitRepository.removeFromStage(file)
            .ifFailure {
                LogManager.getLogger().error("Failed to remove file `$file` from stage with error `${it.error}`.")
            }
            .begin()
    }

    @FXML
    private fun select(event: MouseEvent) {

    }

    @FXML
    private fun open(event: ActionEvent) {

    }

    @FXML
    private fun openInFileBrowser(event: ActionEvent) {

    }


}