package com.harleyoconnor.gitdesk.ui.repository.changes

import com.harleyoconnor.gitdesk.git.repository.Repository
import com.harleyoconnor.gitdesk.ui.UIResource
import com.harleyoconnor.gitdesk.ui.node.SVGIcon
import com.harleyoconnor.gitdesk.ui.util.getIcon
import com.harleyoconnor.gitdesk.ui.util.setOnActions
import com.harleyoconnor.gitdesk.ui.view.ResourceViewLoader
import com.harleyoconnor.gitdesk.ui.view.ViewController
import com.harleyoconnor.gitdesk.util.getUserHome
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
class FileCellController : ViewController<FileCellController.Context> {

    object Loader : ResourceViewLoader<Context, FileCellController, HBox>(
        UIResource("/ui/layouts/repository/changes/FileCell.fxml")
    )

    class Context(val repository: Repository, val file: File) : ViewController.Context

    private lateinit var repository: Repository
    private lateinit var file: File

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

        this.stageCheckbox.setOnActions(this::addToStage, this::removeFromStage)
        this.icon.setupFromSvg(file.getIcon())
        this.nameLabel.text = file.name
        this.nameLabel.tooltip.text = file.absolutePath.replace(getUserHome(), "~")
    }

    private fun addToStage() {
        repository.addToStage(file)
            .ifFailure {
                LogManager.getLogger().error("Failed to stage file `$file` with error `${it.error}`.")
            }
            .begin()
    }

    private fun removeFromStage() {
        repository.removeFromStage(file)
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