package com.harleyoconnor.gitdesk.ui.repository.branch

import com.harleyoconnor.gitdesk.git.repository.Branch
import com.harleyoconnor.gitdesk.git.repository.Repository
import com.harleyoconnor.gitdesk.ui.form.selection.Selection
import com.harleyoconnor.gitdesk.util.process.logFailure
import javafx.application.Platform
import javafx.collections.FXCollections
import javafx.scene.control.ChoiceBox

/**
 * @author Harley O'Connor
 */
class BranchChoiceBox: ChoiceBox<Selection<Branch>>() {

    fun loadChoices(repository: Repository) {
        repository.getAllBranches().ifSuccessful {
            Platform.runLater {
                loadItems(it.result!!)
            }
        }.ifFailure(::logFailure).begin()
    }

    private fun loadItems(branches: Array<Branch>) {
        items = FXCollections.observableArrayList(
            *branches.map { branch ->
                BranchSelection(branch)
            }.toTypedArray()
        )
    }

    private class BranchSelection(val branch: Branch): Selection<Branch> {
        override fun get(): Branch = branch
        override fun toString(): String = branch.name

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as BranchSelection
            // For equality, only names must match.
            return branch.name == other.branch.name
        }

        override fun hashCode(): Int {
            return branch.name.hashCode()
        }
    }

}