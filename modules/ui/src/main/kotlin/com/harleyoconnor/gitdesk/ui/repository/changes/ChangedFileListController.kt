package com.harleyoconnor.gitdesk.ui.repository.changes

import com.harleyoconnor.gitdesk.data.local.LocalRepository
import com.harleyoconnor.gitdesk.git.repository.Repository
import com.harleyoconnor.gitdesk.ui.UIResource
import com.harleyoconnor.gitdesk.ui.translation.TRANSLATIONS_BUNDLE
import com.harleyoconnor.gitdesk.ui.util.setOnActions
import com.harleyoconnor.gitdesk.ui.view.ResourceViewLoader
import com.harleyoconnor.gitdesk.ui.view.ViewController
import com.harleyoconnor.gitdesk.util.process.FunctionalResponse
import com.harleyoconnor.gitdesk.util.process.logFailure
import com.harleyoconnor.gitdesk.util.stream
import com.harleyoconnor.gitdesk.util.toTypedArray
import javafx.application.Platform
import javafx.fxml.FXML
import javafx.scene.Node
import javafx.scene.control.CheckBox
import javafx.scene.control.Label
import javafx.scene.layout.VBox
import java.io.File

/**
 * @author Harley O'Connor
 */
class ChangedFileListController : ViewController<ChangedFileListController.Context>, StagedListener {

    object Loader : ResourceViewLoader<Context, ChangedFileListController, VBox>(
        UIResource("/ui/layouts/repository/changes/ChangedFileList.fxml")
    )

    class Context(val parent: ChangesTabController, val repository: LocalRepository) : ViewController.Context

    private lateinit var parent: ChangesTabController
    lateinit var repository: LocalRepository

    @FXML
    private lateinit var root: VBox

    @FXML
    private lateinit var stageAllCheckbox: CheckBox

    /**
     * `true` if the [stageAllCheckbox] is being updated due to another action, as opposed to direct user input on the
     * box. In this case, listeners should not send requests to stage/un-stage all.
     */
    private var forcedStageAllCheckboxUpdate: Boolean = false

    @FXML
    private lateinit var changedFilesLabel: Label

    override fun setup(context: Context) {
        this.repository = context.repository
        this.parent = context.parent
        stageAllCheckbox.setOnActions(this::stageAll, this::unStageAll)
        onFileStaged() // Causes checkbox to update its state to checked if all files are currently staged.
        refresh()
    }

    override fun onFileStaged() {
        repository.gitRepository.getUnStagedFiles()
            .ifSuccessful {
                updateIfAllFilesStaged(it)
            }
            .ifFailure(::logFailure)
            .begin()
    }

    private fun updateIfAllFilesStaged(it: FunctionalResponse<Array<File>>) {
        if (it.result?.size == 0) {
            Platform.runLater {
                forcedStageAllCheckboxUpdate = true
                stageAllCheckbox.isSelected = true
            }
        }
    }

    override fun onFileUnStaged() {
        if (stageAllCheckbox.isSelected) {
            forcedStageAllCheckboxUpdate = true
            stageAllCheckbox.isSelected = false
        }
    }

    private fun stageAll() {
        if (forcedStageAllCheckboxUpdate) {
            forcedStageAllCheckboxUpdate = false
            return
        }
        repository.gitRepository.stageAllFiles()
            .ifSuccessful {
                Platform.runLater {
                    refresh()
                }
            }
            .ifFailure(::logFailure)
            .begin()
    }

    private fun unStageAll() {
        if (forcedStageAllCheckboxUpdate) {
            forcedStageAllCheckboxUpdate = false
            return
        }
        repository.gitRepository.unStageAllFiles()
            .ifSuccessful {
                Platform.runLater {
                    refresh()
                }
            }
            .ifFailure(::logFailure)
            .begin()
    }

    fun refresh() = this.repository.gitRepository.getChangedFiles()
        .ifSuccessful {
            refreshCells(it.result!!)
        }
        .ifFailure(::logFailure)
        .begin()

    private fun refreshCells(changedFiles: Array<Repository.ChangedFile>) {
        Platform.runLater {
            root.children.remove(1, root.children.size)
            val cells = buildCells(changedFiles)
            root.children.addAll(cells)
            changedFilesLabel.text = TRANSLATIONS_BUNDLE.getString("ui.repository.tab.changes.changed_files")
                .replaceFirst("{}", cells.size.toString())
        }
    }

    private fun buildCells(changedFiles: Array<Repository.ChangedFile>): Array<out Node> = changedFiles.stream()
        .map {
            FileCellController.Loader.load(
                FileCellController.Context(this.repository.gitRepository, it, this)
            ).root
        }
        .toTypedArray()

}