package com.harleyoconnor.gitdesk.ui.menu.clone

import com.harleyoconnor.gitdesk.data.remote.RemoteRepository
import com.harleyoconnor.gitdesk.data.remote.github.search.RepositorySearch
import com.harleyoconnor.gitdesk.git.repository.Remote
import com.harleyoconnor.gitdesk.git.repositoryExistsAt
import com.harleyoconnor.gitdesk.ui.UIResource
import com.harleyoconnor.gitdesk.ui.node.SVGIcon
import com.harleyoconnor.gitdesk.ui.util.load
import com.harleyoconnor.gitdesk.util.xml.SVG
import com.harleyoconnor.gitdesk.util.xml.SVGCache
import javafx.application.Platform.runLater
import javafx.event.ActionEvent
import javafx.event.Event
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.ScrollPane
import javafx.scene.control.TextField
import javafx.scene.layout.VBox
import java.net.URL
import java.util.LinkedList
import java.util.Queue
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors

/**
 * @author Harley O'Connor
 */
class SelectRemoteTabController {

    companion object {
        fun load(parent: CloneTab): VBox {
            val fxml = load<VBox, SelectRemoteTabController>("menu/tabs/clone/SelectRemote")
            fxml.controller.parent = parent
            return fxml.root
        }
    }

    private lateinit var parent: CloneTab

    @FXML
    private lateinit var root: VBox

    @FXML
    private lateinit var searchBar: TextField

    @FXML
    private lateinit var platformToggle: Button
    private var platform: Platform = Platform.GITHUB

    @FXML
    private lateinit var content: VBox

    @FXML
    private lateinit var contentScrollPane: ScrollPane

    private val remoteCellsRefreshExecutor by lazy {
        Executors.newSingleThreadExecutor()
    }

    private var lastQuery: String = ""

    @FXML
    fun initialize() {
        contentScrollPane.vvalueProperty().addListener { _, _, new ->
            if (new == 1.0) {
                platform.cellLoader.loadMore(this, searchBar.text)
            }
        }
    }

    fun togglePlatform(event: ActionEvent) {
        platform = Platform.values()[(platform.ordinal + 1) % Platform.values().size]
        updatePlatformToggleGraphic()
        clearDisplayedRepositories()
        onSearchQueryUpdated(searchBar.text)
    }

    private fun updatePlatformToggleGraphic() {
        val icon = SVGIcon()
        icon.prefWidth = 16.0
        icon.prefHeight = 16.0
        icon.styleClass.add("icon")
        icon.setupFromSvg(platform.icon())
        platformToggle.graphic = icon
    }

    fun onSearchQueryUpdated(event: Event) {
        val query = searchBar.text
        if (query == lastQuery) {
            return
        }
        onSearchQueryUpdated(query)
        lastQuery = query
    }

    private fun onSearchQueryUpdated(newQuery: String) {
        platform.cellLoader.searchQueryUpdated(this, newQuery)
    }

    private fun clearDisplayedRepositories() {
        this.content.children.clear()
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
        fun searchQueryUpdated(controller: SelectRemoteTabController, newQuery: String)

        fun loadMore(controller: SelectRemoteTabController, query: String)
    }

    object GitHubRemoteCellLoader : RemoteCellLoader {
        private val pendingFutures: Queue<CompletableFuture<*>> = LinkedList()
        private var nextPage = 1

        override fun searchQueryUpdated(controller: SelectRemoteTabController, newQuery: String) {
            clearPendingTasks()
            if (newQuery.isNotEmpty()) {
                queueSearchTask(controller, newQuery)
            }
        }

        override fun loadMore(controller: SelectRemoteTabController, query: String) {
            if (query.isNotEmpty()) {
                queueSearchTask(controller, query)
            }
        }

        private fun clearPendingTasks() {
            pendingFutures.forEach { it.cancel(true) }
            pendingFutures.clear()
            nextPage = 1
        }

        private fun queueSearchTask(controller: SelectRemoteTabController, query: String, page: Int = nextPage++) {
            val future = CompletableFuture.runAsync({ Thread.sleep(300) }, controller.remoteCellsRefreshExecutor)
                .thenApply { runSearch(query, page) }
            future.thenAccept { results ->
                queueUpdateDisplayedResults(results, controller, page)
            }.thenRun {
                pendingFutures.remove(future)
            }
            pendingFutures.add(future)
        }

        @Suppress("UNCHECKED_CAST")
        private fun runSearch(query: String, page: Int): Array<RemoteRepository>? {
            return RepositorySearch(query = query, perPage = 50, page = page).run()?.let {
                it.items as Array<RemoteRepository>
            }
        }

        private fun queueUpdateDisplayedResults(
            results: Array<RemoteRepository>?,
            controller: SelectRemoteTabController,
            page: Int
        ) {
            results?.let {
                runLater {
                    if (page == 1) {
                        controller.clearDisplayedRepositories()
                    }
                    displayResults(controller, it)
                }
            }
        }

        private fun displayResults(controller: SelectRemoteTabController, remotes: Array<RemoteRepository>) {
            val children = controller.content.children
            children.lastOrNull()?.styleClass?.remove("bottom")
            children.addAll(
                remotes.map { SelectableRemoteCellController.loadCell(controller.parent, it) }
            )
            children.firstOrNull()?.styleClass?.add("top")
            children.lastOrNull()?.styleClass?.add("bottom")
        }
    }

    object UrlRemoteCellLoader : RemoteCellLoader {
        override fun searchQueryUpdated(controller: SelectRemoteTabController, newQuery: String) {
            CompletableFuture.runAsync({
                val url = getUrlFromQuery(newQuery)
                if (!repositoryExistsAt(url)) {
                    return@runAsync
                }
                queueDisplayResults(controller, url)
            }, controller.remoteCellsRefreshExecutor)
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
                controller.clearDisplayedRepositories()
                controller.content.children.add(
                    SelectableRemoteCellController.loadCell(controller.parent, Remote.getRemote(url))
                )
            }
        }
    }
}