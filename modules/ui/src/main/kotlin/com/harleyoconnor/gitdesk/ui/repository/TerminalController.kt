package com.harleyoconnor.gitdesk.ui.repository

import com.harleyoconnor.gitdesk.data.local.LocalRepository
import com.harleyoconnor.gitdesk.ui.UIResource
import com.harleyoconnor.gitdesk.ui.util.exceptionallyOnMainThread
import com.harleyoconnor.gitdesk.ui.util.thenAcceptOnMainThread
import com.harleyoconnor.gitdesk.ui.view.ResourceViewLoader
import com.harleyoconnor.gitdesk.ui.view.ViewController
import com.harleyoconnor.gitdesk.util.getUserName
import javafx.application.Platform
import javafx.fxml.FXML
import javafx.scene.control.ScrollPane
import javafx.scene.layout.VBox
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.InetAddress
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors


/**
 * @author Harley O'Connor
 */
class TerminalController : ViewController<TerminalController.Context> {

    object Loader : ResourceViewLoader<Context, TerminalController, ScrollPane>(
        UIResource("/ui/layouts/repository/Terminal.fxml")
    )

    class Context(val repository: LocalRepository) : ViewController.Context

    private val executor = Executors.newSingleThreadExecutor()
    private lateinit var repository: LocalRepository

    @FXML
    private lateinit var scrollPane: ScrollPane

    @FXML
    private lateinit var linesBox: VBox

    override fun setup(context: Context) {
        repository = context.repository
        this.addInputLine()
    }

    private fun addInputLine() {
        val lineView = TerminalLineController.Loader.load(
            TerminalLineController.Context(
                repository,
                getUserName() + "@" + InetAddress.getLocalHost().hostName + " " +
                        repository.directory.name + " % ",
                this::startNewProcess
            )
        )
        linesBox.children.add(
            lineView.root
        )
        lineView.controller.focus()
    }

    private fun startNewProcess(command: String) {
        CompletableFuture.runAsync({
            val process = ProcessBuilder()
                .command(command.split(' '))
                .directory(repository.directory)
                .redirectErrorStream(true)
                .start()
            val lines = BufferedReader(InputStreamReader(process.inputStream)).readLines()
            Platform.runLater {
                loadOutput(lines)
            }
        }, executor)
            .exceptionallyOnMainThread {
                it.message?.let { message ->
                    addOutputLine(message)
                }
                addInputLine()
            }
    }

    private fun loadOutput(lines: List<String>) {
        lines.forEach {
            addOutputLine(it)
        }
        addInputLine()
        // Scroll to bottom of terminal and focus on next input line. Workaround for scroll pane needing time to
        // recalculate hangs another thread for 100ms before scrolling to the bottom on the main thrad.
        CompletableFuture.runAsync {
            Thread.sleep(100)
        }.thenAcceptOnMainThread {
            toBottom()
        }
        clearOld()
    }

    private fun addOutputLine(text: String) {
        linesBox.children.add(
            TerminalLineController.Loader.load(
                TerminalLineController.Context(repository, text, null)
            ).root
        )
    }

    private fun toBottom() {
        scrollPane.vvalue = 1.0
    }

    private fun clearOld() {
        if (linesBox.children.size > 1000) {
            linesBox.children.remove(0, linesBox.children.size - 1000)
        }
    }


}