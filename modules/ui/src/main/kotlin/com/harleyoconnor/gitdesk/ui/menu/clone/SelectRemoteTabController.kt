package com.harleyoconnor.gitdesk.ui.menu.clone

import com.harleyoconnor.gitdesk.data.remote.Platform
import com.harleyoconnor.gitdesk.data.remote.RemoteRepository
import com.harleyoconnor.gitdesk.data.remote.github.GitHubNetworking
import com.harleyoconnor.gitdesk.git.repository.Remote
import com.harleyoconnor.gitdesk.git.repositoryExistsAt
import com.harleyoconnor.gitdesk.ui.Application
import com.harleyoconnor.gitdesk.ui.UIResource
import com.harleyoconnor.gitdesk.ui.node.RadioContextMenu
import com.harleyoconnor.gitdesk.ui.node.RemoteCellList
import com.harleyoconnor.gitdesk.ui.node.SVGIcon
import com.harleyoconnor.gitdesk.ui.translation.TRANSLATIONS_BUNDLE
import com.harleyoconnor.gitdesk.ui.util.exceptionallyOnMainThread
import com.harleyoconnor.gitdesk.ui.util.flip
import com.harleyoconnor.gitdesk.ui.util.logErrorAndCreateDialogue
import com.harleyoconnor.gitdesk.ui.util.thenAcceptOnMainThread
import com.harleyoconnor.gitdesk.ui.util.whenScrolledToBottom
import com.harleyoconnor.gitdesk.ui.view.ResourceViewLoader
import com.harleyoconnor.gitdesk.ui.view.ViewController
import com.harleyoconnor.gitdesk.util.addIfAbsent
import com.harleyoconnor.gitdesk.util.xml.SVG
import com.harleyoconnor.gitdesk.util.xml.SVGCache
import javafx.application.Platform.runLater
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.geometry.Side
import javafx.scene.control.Button
import javafx.scene.control.ScrollPane
import javafx.scene.control.TextField
import javafx.scene.input.KeyCode
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import org.fxmisc.wellbehaved.event.EventPattern
import org.fxmisc.wellbehaved.event.InputMap
import org.fxmisc.wellbehaved.event.Nodes
import java.net.URL
import java.util.concurrent.CompletableFuture

/**
 * @author Harley O'Connor
 */
class SelectRemoteTabController : ViewController<SelectRemoteTabController.Context> {

    object Loader : ResourceViewLoader<Context, SelectRemoteTabController, VBox>(
        UIResource("/ui/layouts/menu/clone/SelectRemote.fxml")
    )

    class Context(val parent: CloneTab) : ViewController.Context

    private lateinit var parent: CloneTab
    @FXML
    private lateinit var root: VBox

    @FXML
    private lateinit var searchBar: TextField

    @FXML
    private lateinit var searchOptionsBox: HBox

    @FXML
    private lateinit var sortButton: Button

    private var sort: RemoteRepository.Sort = RemoteRepository.Sort.BEST_MATCH
        set(value) {
            field = value; updateSearchResults()
        }

    private val sortContextMenu by lazy {
        RadioContextMenu(
            RemoteRepository.Sort.values().associateBy {
                TRANSLATIONS_BUNDLE.getString("sort.${it.name.lowercase()}")
            },
            RemoteRepository.Sort.BEST_MATCH
        ) {
            sort = it
        }
    }

    @FXML
    private lateinit var toggleSortOrderButton: Button

    @FXML
    private lateinit var sortOrderIcon: SVGIcon

    private var sortOrder: RemoteRepository.SortOrder = RemoteRepository.SortOrder.DESCENDING
        set(value) {
            field = value; updateSearchResults()
        }

    @FXML
    private lateinit var platformToggle: Button

    private var platform: Platform = Platform.GITHUB

    @FXML
    private lateinit var content: RemoteCellList

    @FXML
    private lateinit var contentScrollPane: ScrollPane

    private var searchQuery: String = ""

    @FXML
    fun initialize() {
        content.setOnElementSelected { event ->
            parent.toLocationSelection(event.element)
        }
        Nodes.addInputMap(searchBar, InputMap.sequence(
            InputMap.consume(EventPattern.keyPressed(KeyCode.ENTER)) {
                if (!updateSearchResults(searchBar.text)) {
                    content.select()
                }
            },
            InputMap.consume(EventPattern.keyPressed(KeyCode.UP)) {
                content.moveSelectionUp()
            },
            InputMap.consume(EventPattern.keyPressed(KeyCode.DOWN)) {
                content.moveSelectionDown()
            }
        ))
        contentScrollPane.whenScrolledToBottom {
            platform.cellLoader.loadMore(this, searchBar.text)
        }
    }

    override fun setup(context: Context) {
        this.parent = context.parent
    }

    @FXML
    private fun openSortMenu(event: ActionEvent) {
        sortContextMenu.show(searchOptionsBox, Side.BOTTOM, 0.0, 0.0)
    }

    @FXML
    private fun toggleSortOrder(event: ActionEvent) {
        sortOrder = sortOrder.other()
        sortOrderIcon.flip()
    }

    @FXML
    private fun togglePlatform() {
        platform = Platform.values()[(platform.ordinal + 1) % Platform.values().size]
        updateShowingSortButtons()
        updatePlatformToggleGraphic()
        clearResults()
        updateSearchResults(searchBar.text)
    }

    private fun updatePlatformToggleGraphic() {
        val icon = SVGIcon()
        icon.prefWidth = 16.0
        icon.prefHeight = 16.0
        icon.styleClass.add("icon")
        icon.setupFromSvg(platform.icon())
        platformToggle.graphic = icon
    }

    private fun updateShowingSortButtons() {
        if (platform.cellLoader.supportsSorting) {
            searchOptionsBox.children.addIfAbsent(0, sortButton)
            searchOptionsBox.children.addIfAbsent(1, toggleSortOrderButton)
        } else {
            searchOptionsBox.children.removeAll(sortButton, toggleSortOrderButton)
        }
    }

    /**
     * @return `true` if a search will be completed
     */
    private fun updateSearchResults(query: String): Boolean {
        if (query == this.searchQuery || query.isEmpty()) {
            return false
        }
        this.searchQuery = query
        updateSearchResults()
        return true
    }

    private fun updateSearchResults() {
        clearResults()
        platform.cellLoader.updateSearchResults(this, searchQuery)
    }

    private fun clearResults() {
        this.content.clear()
    }

    enum class Platform(
        val icon: () -> SVG,
        val cellLoader: RemoteCellLoader
    ) {
        GITHUB({ getOrLoad("github") }, GitHubRemoteCellLoader),
        URL({ getOrLoad("web") }, UrlRemoteCellLoader);

        companion object {
            fun getOrLoad(iconName: String): SVG {
                return SVGCache.getOrLoad(UIResource("/ui/icons/$iconName.svg"))
            }
        }
    }

    interface RemoteCellLoader {
        val supportsSorting: Boolean

        fun updateSearchResults(controller: SelectRemoteTabController, newQuery: String)

        fun loadMore(controller: SelectRemoteTabController, query: String)
    }

    object GitHubRemoteCellLoader : RemoteCellLoader {
        private var nextPage = 1

        override val supportsSorting: Boolean = true

        override fun updateSearchResults(controller: SelectRemoteTabController, newQuery: String) {
            nextPage = 1
            if (newQuery.isNotEmpty()) {
                loadNextPage(controller, newQuery)
            }
        }

        override fun loadMore(controller: SelectRemoteTabController, query: String) {
            if (query.isNotEmpty()) {
                loadNextPage(controller, query)
            }
        }

        private fun loadNextPage(
            controller: SelectRemoteTabController,
            query: String
        ) {
            loadPage(controller, query, nextPage++)
        }

        private fun loadPage(controller: SelectRemoteTabController, query: String, page: Int) {
            getRemotes(query, controller.sort, controller.sortOrder, page)
                .thenApply {
                    loadCells(it, controller)
                }
                .thenAcceptOnMainThread {
                    displayCells(it, controller)
                }
                .exceptionallyOnMainThread {
                    logErrorAndCreateDialogue("dialogue.error.searching_repositories", it).show()
                }
        }

        @Suppress("UNCHECKED_CAST")
        private fun getRemotes(
            query: String,
            sort: RemoteRepository.Sort,
            sortOrder: RemoteRepository.SortOrder,
            page: Int
        ): CompletableFuture<Array<out RemoteRepository>> {
            return GitHubNetworking.getRepositories(
                query,
                sort,
                sortOrder,
                page,
                Application.getInstance().backgroundExecutor
            )
        }

        private fun loadCells(
            repositories: Array<out RemoteRepository>,
            controller: SelectRemoteTabController
        ): Map<RemoteRepository, HBox> {
            return repositories.associateWith {
                SelectableRemoteCellController.Loader.load(
                    SelectableRemoteCellController.Context(
                        controller.parent,
                        it
                    )
                ).root
            }
        }

        fun displayCells(cells: Map<RemoteRepository, HBox>, controller: SelectRemoteTabController) {
            cells.forEach { cell ->
                controller.content.addElement(cell.key, cell.value)
            }
        }
    }

    object UrlRemoteCellLoader : RemoteCellLoader {
        override val supportsSorting: Boolean = false

        override fun updateSearchResults(controller: SelectRemoteTabController, newQuery: String) {
            CompletableFuture.runAsync({
                val url = getUrlFromQuery(newQuery)
                if (!repositoryExistsAt(url)) {
                    return@runAsync
                }
                queueDisplayResults(controller, url)
            }, Application.getInstance().backgroundExecutor)
        }

        override fun loadMore(controller: SelectRemoteTabController, query: String) {
        }

        private fun getUrlFromQuery(newQuery: String): URL {
            val urlString = if (!newQuery.endsWith(".git")) {
                "$newQuery.git"
            } else newQuery
            return URL(urlString)
        }

        private fun queueDisplayResults(
            controller: SelectRemoteTabController,
            url: URL
        ) {
            runLater {
                controller.clearResults()
                val remote = Remote.getRemote(url)
                controller.content.addElement(
                    remote,
                    SelectableRemoteCellController.Loader.load(
                        SelectableRemoteCellController.Context(
                            controller.parent,
                            remote
                        )
                    ).root
                )
            }
        }
    }
}