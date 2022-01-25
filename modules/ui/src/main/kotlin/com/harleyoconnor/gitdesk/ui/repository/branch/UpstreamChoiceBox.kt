package com.harleyoconnor.gitdesk.ui.repository.branch

import com.harleyoconnor.gitdesk.git.repository.RemoteBranch
import com.harleyoconnor.gitdesk.git.repository.Repository
import com.harleyoconnor.gitdesk.ui.form.selection.NoSelection
import com.harleyoconnor.gitdesk.ui.form.selection.Selection
import com.harleyoconnor.gitdesk.ui.form.selection.SelectionSeparator
import com.harleyoconnor.gitdesk.util.process.logFailure
import javafx.application.Platform
import javafx.collections.FXCollections
import javafx.scene.control.ChoiceBox
import java.util.Objects

/**
 * @author Harley O'Connor
 */
class UpstreamChoiceBox: ChoiceBox<Selection<RemoteBranch>>() {

    fun loadChoices(repository: Repository) {
        repository.getRemoteBranches().ifSuccessful {
            it.result?.let { upstreams ->
                Platform.runLater {
                    loadItems(upstreams)
                }
            }
        }.ifFailure(::logFailure).begin()
    }

    private fun loadItems(upstreams: Array<RemoteBranch>) {
        items = FXCollections.observableArrayList(
            UpstreamSelection.NONE,
            UpstreamSelection.SEPARATOR,
            *upstreams.map { upstream ->
                UpstreamSelection(upstream)
            }.toTypedArray()
        )
    }

    fun select(upstream: RemoteBranch) {
        selectionModel.select(UpstreamSelection(upstream))
    }

    private class UpstreamSelection(val upstream: RemoteBranch): Selection<RemoteBranch> {
        companion object {
            val NONE = NoSelection<RemoteBranch>()
            val SEPARATOR get() = SelectionSeparator<RemoteBranch>()
        }

        override fun get(): RemoteBranch = upstream
        override fun toString(): String = upstream.remote.name + "/" + upstream.name

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as UpstreamSelection
            // For equality, the local reference name for the branch and remote must be the same.
            return upstream.name == other.upstream.name && upstream.remote.name == other.upstream.remote.name
        }

        override fun hashCode(): Int {
            return Objects.hash(upstream.name, upstream.remote.name)
        }
    }

}