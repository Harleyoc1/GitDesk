package com.harleyoconnor.gitdesk.ui.menu.clone

import com.harleyoconnor.gitdesk.data.remote.RemoteRepository
import com.harleyoconnor.gitdesk.data.remote.github.search.RepositorySearch
import com.harleyoconnor.gitdesk.git.repository.Remote
import com.harleyoconnor.gitdesk.git.repositoryExistsAt
import com.harleyoconnor.gitdesk.ui.UIResource
import com.harleyoconnor.gitdesk.ui.node.SVGIcon
import com.harleyoconnor.gitdesk.util.xml.SVG
import com.harleyoconnor.gitdesk.util.xml.SVGCache
import javafx.application.Platform.runLater
import javafx.event.ActionEvent
import javafx.event.Event
import javafx.fxml.FXML
import javafx.scene.control.Button
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

    @FXML
    private lateinit var searchBar: TextField

    @FXML
    private lateinit var platformToggle: Button
    private var platform: Platform = Platform.GITHUB

    @FXML
    private lateinit var content: VBox

    private val remoteCellsRefreshExecutor by lazy {
        Executors.newSingleThreadExecutor()
    }

    private var lastQuery: String = ""

    fun togglePlatform(actionEvent: ActionEvent) {
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
        platform.cellRefresher.searchQueryUpdated(this, newQuery)
    }

    private fun clearDisplayedRepositories() {
        this.content.children.clear()
    }

    enum class Platform(
        val icon: () -> SVG,
        val cellRefresher: CellRefresher
    ) {
        GITHUB({ getOrLoad("github") }, GitHubCellRefresher),
        URL({ getOrLoad("web") }, UrlCellRefresher);

        companion object {
            fun getOrLoad(iconName: String): SVG {
                return SVGCache.getOrLoad(UIResource("/ui/icons/$iconName.svg"))
            }
        }
    }

    interface CellRefresher {
        fun searchQueryUpdated(controller: SelectRemoteTabController, newQuery: String)
    }

    object GitHubCellRefresher : CellRefresher {
        private val pendingFutures: Queue<CompletableFuture<*>> = LinkedList()

        override fun searchQueryUpdated(controller: SelectRemoteTabController, newQuery: String) {
            clearPendingTasks()
            if (newQuery.isNotEmpty()) {
                computeSearchTask(controller, newQuery)
            }
        }

        private fun clearPendingTasks() {
            pendingFutures.forEach { it.cancel(true) }
            pendingFutures.clear()
        }

        private fun computeSearchTask(controller: SelectRemoteTabController, query: String, page: Int = 1) {
            val future = CompletableFuture.runAsync({ Thread.sleep(300) }, controller.remoteCellsRefreshExecutor)
                .thenApply { runSearch(query, page) }
            future.thenAccept { results ->
                queueUpdateDisplayedResults(results, controller)
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
            controller: SelectRemoteTabController
        ) {
            results?.let {
                runLater {
                    controller.clearDisplayedRepositories()
                    displayResults(controller, it)
                }
            }
        }

        private fun displayResults(controller: SelectRemoteTabController, remotes: Array<RemoteRepository>) {
            controller.content.children.addAll(
                remotes.map { RemoteCellController.loadCell(it) }
            )
        }
    }

    object UrlCellRefresher : CellRefresher {
        override fun searchQueryUpdated(controller: SelectRemoteTabController, newQuery: String) {
            CompletableFuture.runAsync({
                val url = getUrlFromQuery(newQuery)
                if (!repositoryExistsAt(url)) {
                    return@runAsync
                }
                queueDisplayResults(controller, url)
            }, controller.remoteCellsRefreshExecutor)
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
                    RemoteCellController.loadCell(Remote.getRemote(url))
                )
            }
        }
    }
}