package com.harleyoconnor.gitdesk.ui.window

import com.harleyoconnor.gitdesk.data.Data
import com.harleyoconnor.gitdesk.ui.Application
import com.harleyoconnor.gitdesk.ui.menu.MenuWindow
import com.harleyoconnor.gitdesk.ui.style.DynamicStylesheetManager
import com.harleyoconnor.gitdesk.ui.style.Stylesheet
import com.harleyoconnor.gitdesk.ui.style.StylesheetManager
import com.harleyoconnor.gitdesk.ui.style.Stylesheets
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.input.KeyCode
import javafx.stage.Stage

/**
 * @author Harley O'Connor
 */
abstract class AbstractWindow(
    val stage: Stage,
    root: Parent,
    protected val manager: WindowManager
) : Window {

    protected var root: Parent = root
        set(value) {
            field = value
            scene.root = value
        }

    protected val scene: Scene = Scene(root)

    private val stylesheetManager: StylesheetManager = DynamicStylesheetManager(scene)

    protected open val stylesheets: Array<Stylesheet> get() = arrayOf(Stylesheets.DEFAULT, Stylesheets.DEFAULT_THEMED)

    init {
        this.setStageBounds()
        stylesheetManager.registerSheets(*stylesheets)
        stage.scene = scene
        scene.setOnKeyPressed {
            if (it.code == KeyCode.Q && it.isShortcutDown) { // TODO: Alt+F4 for Windows
                onCloseAppRequested()
            }
        }
        stage.setOnCloseRequest {
            manager.setClosed(this)
            postClose()
        }
    }

    private fun setStageBounds() {
        this.stage.minWidth = this.minWidth
        this.stage.minHeight = this.minHeight
        this.stage.maxWidth = this.maxWidth
        this.stage.maxHeight = this.maxHeight
        this.stage.isResizable = this.resizable
    }

    override fun open() {
        stage.show()
        manager.setOpen(this)
    }

    override fun focus() {
        stage.requestFocus()
    }

    override fun close() {
        stage.close()
        manager.setClosed(this)
        postClose()
    }

    protected open fun postClose() {
        closeAndSaveResources()
        if (manager.noWindowsOpen()) {
            MenuWindow(Stage()).open()
        }
    }

    protected open fun onCloseAppRequested() {
        Application.getInstance().close()
    }

}