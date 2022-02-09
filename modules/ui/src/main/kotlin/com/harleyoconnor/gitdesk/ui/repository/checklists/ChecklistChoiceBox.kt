package com.harleyoconnor.gitdesk.ui.repository.checklists

import com.harleyoconnor.gitdesk.data.remote.RemoteRepository
import com.harleyoconnor.gitdesk.data.remote.checklist.Checklist
import com.harleyoconnor.gitdesk.data.remote.checklist.getChecklists
import com.harleyoconnor.gitdesk.ui.form.selection.Selection
import com.harleyoconnor.gitdesk.ui.util.exceptionallyOnMainThread
import com.harleyoconnor.gitdesk.ui.util.logErrorAndCreateDialogue
import com.harleyoconnor.gitdesk.ui.util.thenAcceptOnMainThread
import javafx.collections.FXCollections
import javafx.scene.control.ChoiceBox

/**
 * @author Harley O'Connor
 */
class ChecklistChoiceBox : ChoiceBox<Selection<Checklist>>() {

    fun loadChoices(remote: RemoteRepository) {
        getChecklists(remote)
            .thenAcceptOnMainThread {
                loadItems(it)
                selectionModel.selectFirst()
            }
            .exceptionallyOnMainThread {
                logErrorAndCreateDialogue("dialogue.error.retrieving_checklists", it)
            }
    }

    private fun loadItems(checklists: List<Checklist>) {
        items = FXCollections.observableArrayList(
            *checklists.map { branch ->
                ChecklistSelection(branch)
            }.toTypedArray()
        )
    }

    fun addChoice(checklist: Checklist) {
        items.add(ChecklistSelection(checklist))
    }

    fun select(checklist: Checklist) {
        selectionModel.select(ChecklistSelection(checklist))
    }

    private class ChecklistSelection(val checklist: Checklist): Selection<Checklist> {
        override fun get(): Checklist = checklist
        override fun toString(): String = checklist.name

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as ChecklistSelection
            // For equality, only names must match.
            return checklist.name == other.checklist.name
        }

        override fun hashCode(): Int {
            return checklist.name.hashCode()
        }
    }

}