package com.harleyoconnor.gitdesk.ui.menu

import com.harleyoconnor.gitdesk.data.repository.LocalRepository
import com.harleyoconnor.gitdesk.ui.util.loaderForLayout
import com.harleyoconnor.gitdesk.util.getUserHome
import javafx.fxml.FXML
import javafx.scene.control.Label
import javafx.scene.input.MouseEvent
import javafx.scene.layout.HBox

/**
 *
 * @author Harley O'Connor
 */
class RepositoryCellController {

    companion object {
        fun loadCell(repository: LocalRepository): HBox {
            val loader = loaderForLayout("menu/RepositoryCell")
            val cell = loader.load<HBox>()
            loader.getController<RepositoryCellController>().setRepository(repository)
            return cell
        }
    }

    @FXML
    private lateinit var label: Label

    // TODO: Remote info on left, path label underneath.
    @FXML
    private lateinit var directoryLabel: Label

    private lateinit var repository: LocalRepository

    private fun setRepository(repository: LocalRepository) {
        this.repository = repository
        label.text = repository.id
        directoryLabel.text = repository.directory.path.replace(getUserHome(), "~")
    }

    @FXML
    fun openRepository(event: MouseEvent) {

    }

}