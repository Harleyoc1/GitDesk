package com.harleyoconnor.gitdesk.ui.menu.open

import com.harleyoconnor.gitdesk.data.Data
import com.harleyoconnor.gitdesk.data.local.LocalRepository
import com.harleyoconnor.gitdesk.git.initRepository
import com.harleyoconnor.gitdesk.git.repositoryExistsAt
import com.harleyoconnor.gitdesk.ui.UIResource
import com.harleyoconnor.gitdesk.ui.menu.MenuController
import com.harleyoconnor.gitdesk.ui.node.RepositoryCellList
import com.harleyoconnor.gitdesk.ui.settings.AppSettings
import com.harleyoconnor.gitdesk.ui.view.ResourceViewLoader
import com.harleyoconnor.gitdesk.ui.view.ViewController
import com.harleyoconnor.gitdesk.util.Directory
import javafx.fxml.FXML
import javafx.scene.control.TextField
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.stage.DirectoryChooser
import org.fxmisc.wellbehaved.event.EventPattern.keyPressed
import org.fxmisc.wellbehaved.event.InputMap
import org.fxmisc.wellbehaved.event.InputMap.consume
import org.fxmisc.wellbehaved.event.Nodes

/**
 * @author Harley O'Connor
 */
class OpenTabController : ViewController<OpenTabController.Context> {

    object Loader : ResourceViewLoader<Context, OpenTabController, VBox>(
        UIResource("/ui/layouts/menu/open/OpenTab.fxml")
    )

    class Context(val parent: MenuController) : ViewController.Context

    private lateinit var parent: MenuController

    @FXML
    private lateinit var content: RepositoryCellList

    @FXML
    private lateinit var searchBar: TextField
    private var lastSearchQuery = ""

    private val cellsCache: MutableMap<LocalRepository, HBox> = mutableMapOf()

    @FXML
    private fun initialize() {
        Nodes.addInputMap(searchBar, InputMap.sequence(
            consume(keyPressed(KeyCode.UP)) {
                content.moveSelectionUp()
            },
            consume(keyPressed(KeyCode.DOWN)) {
                content.moveSelectionDown()
            },
            consume(keyPressed(KeyCode.ENTER)) {
                content.select()
            }
        ))
    }

    override fun setup(context: Context) {
        this.parent = context.parent
        this.content.setOnElementSelected { event ->
            parent.openRepository(event.element)
        }
        displayRepositories("")
    }

    fun selectLocalRepository() {
        val directoryChooser = DirectoryChooser()
        directoryChooser.showDialog(parent.stage)?.let {
            addDirectory(Directory(it))
        }
    }

    private fun addDirectory(directory: Directory) {
        if (!repositoryExistsAt(directory)) {
            initRepository(directory)
        }
        parent.openRepository(
            LocalRepository(
                directory.name,
                directory,
                null,
                AppSettings.get().getOrLoad().repositories.showHiddenFilesByDefault
            )
        )
    }

    @FXML
    private fun onSearchQueryUpdated(keyEvent: KeyEvent) {
        val query = searchBar.text
        if (lastSearchQuery == query) {
            return
        }
        lastSearchQuery = query
        clearDisplayedRepositories()
        displayRepositories(query)
    }

    private fun clearDisplayedRepositories() {
        this.content.clear()
    }

    private fun displayRepositories(searchQuery: String) {
        Data.repositoryAccess.getAll().forEach { (directory, id) ->
            if (matchesQuery(id, searchQuery)) {
                displayRepository(Data.repositoryAccess.get(directory))
            }
        }
    }

    @Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE")
    private fun displayRepository(repository: LocalRepository) {
        content.addElement(repository, cellsCache.computeIfAbsent(repository) {
            RepositoryCellController.Loader.load(RepositoryCellController.Context(this.parent, it)).root
        })
    }

    private fun matchesQuery(name: String, query: String) = name.lowercase().contains(query.lowercase())

}