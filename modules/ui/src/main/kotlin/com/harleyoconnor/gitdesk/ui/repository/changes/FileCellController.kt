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

/**
 * @author Harley O'Connor
 */
class FileCellController : ViewController<FileCellController.Context> {

    object Loader : ResourceViewLoader<Context, FileCellController, HBox>(
        UIResource("/ui/layouts/repository/changes/FileCell.fxml")
    )

    class Context(val repository: Repository, val file: Repository.ChangedFile, val stagedListener: StagedListener) :
        ViewController.Context

    private lateinit var repository: Repository
    private lateinit var file: Repository.ChangedFile
    private lateinit var stagedListener: StagedListener

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
        this.stagedListener = context.stagedListener

        this.stageCheckbox.isSelected = file.staged
        this.stageCheckbox.setOnActions(this::addToStage, this::removeFromStage)
        this.icon.setupFromSvg(file.file.getIcon())
        this.nameLabel.text = file.file.name
        this.nameLabel.tooltip.text = file.file.absolutePath.replace(getUserHome(), "~")
    }

    private fun addToStage() {
        repository.addToStage(file.file)
            .ifSuccessful {
                stagedListener.onFileStaged()
            }
            .ifFailure {
                LogManager.getLogger().error("Failed to stage file `${file.file}` with error `${it.error}`.")
            }
            .begin()
    }

    private fun removeFromStage() {
        repository.removeFromStage(file.file)
            .ifSuccessful {
                stagedListener.onFileUnStaged()
            }
            .ifFailure {
                LogManager.getLogger()
                    .error("Failed to remove file `${file.file}` from stage with error `${it.error}`.")
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