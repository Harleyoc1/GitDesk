package com.harleyoconnor.gitdesk.ui.repository.branch

import com.harleyoconnor.gitdesk.git.repository.Branch
import com.harleyoconnor.gitdesk.git.repository.Repository
import com.harleyoconnor.gitdesk.ui.node.SVGIcon
import com.harleyoconnor.gitdesk.ui.style.CHECKED_OUT_PSEUDO_CLASS
import com.harleyoconnor.gitdesk.ui.util.load
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.Label
import javafx.scene.input.MouseEvent
import javafx.scene.layout.HBox

/**
 *
 * @author Harley O'Connor
 */
class BranchCellController {

    companion object {
        fun load(parent: BranchesController, branch: Branch): HBox {
            val fxml = load<HBox, BranchCellController>("repository/branches/BranchCell")
            fxml.controller.setup(parent, branch)
            return fxml.root
        }
    }

    private lateinit var parent: BranchesController

    private lateinit var branch: Branch

    @FXML
    private lateinit var root: HBox
    @FXML
    private lateinit var label: Label
    @FXML
    private lateinit var checkedOutIcon: SVGIcon
    @FXML
    private lateinit var remoteLabel: Label
    @FXML
    private lateinit var remotePlatformIcon: SVGIcon

    private fun setup(parent: BranchesController, branch: Branch) {
        this.parent = parent
        this.branch = branch
        label.text = branch.name
        checkedOutIcon.pseudoClassStateChanged(CHECKED_OUT_PSEUDO_CLASS, branch.checkedOut)
    }

    @FXML
    private fun onCellPressed(event: MouseEvent) {

    }

    @FXML
    private fun checkout(event: ActionEvent) {

    }
}
