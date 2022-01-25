package com.harleyoconnor.gitdesk.ui.repository.changes

import com.harleyoconnor.gitdesk.data.local.LocalRepository
import com.harleyoconnor.gitdesk.ui.UIResource
import com.harleyoconnor.gitdesk.ui.view.ResourceViewLoader
import com.harleyoconnor.gitdesk.ui.view.ViewController
import javafx.fxml.FXML
import javafx.scene.control.ScrollPane
import javafx.scene.control.SplitPane
import javafx.scene.control.TabPane
import javafx.stage.Stage

/**
 * @author Harley O'Connor
 */
class ChangesTabController : ViewController<ChangesTabController.Context> {

    object Loader: ResourceViewLoader<Context, ChangesTabController, SplitPane>(
        UIResource("/ui/layouts/repository/changes/Root.fxml")
    )

    class Context(val stage: Stage, val repository: LocalRepository): ViewController.Context

    private lateinit var stage: Stage
    private lateinit var repository: LocalRepository

    @FXML
    private lateinit var fileList: ScrollPane

    @FXML
    private lateinit var commitArea: ScrollPane

    @FXML
    private lateinit var fileTabs: TabPane

    @FXML
    private fun initialize() {
        fileTabs.tabDragPolicy = TabPane.TabDragPolicy.REORDER
    }

    override fun setup(context: Context) {
        this.stage = context.stage
        this.repository = context.repository
        fileList.content = ChangedFileListController.Loader.load(
            ChangedFileListController.Context(this, repository)
        ).root
    }


}