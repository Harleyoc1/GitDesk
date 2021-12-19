package com.harleyoconnor.gitdesk.ui.node

import com.harleyoconnor.gitdesk.data.local.LocalRepository
import javafx.event.Event
import javafx.event.EventHandler
import javafx.event.EventType
import javafx.scene.Node
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.layout.VBox

/**
 *
 * @author Harley O'Connor
 */
class RepositoryCellList : SelectionCellList<LocalRepository>()