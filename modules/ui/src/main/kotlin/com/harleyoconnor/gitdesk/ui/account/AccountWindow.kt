package com.harleyoconnor.gitdesk.ui.account

import com.harleyoconnor.gitdesk.ui.Application
import com.harleyoconnor.gitdesk.ui.util.loadLayout
import com.harleyoconnor.gitdesk.ui.window.AbstractWindow
import javafx.stage.Stage

/**
 *
 * @author Harley O'Connor
 */
class AccountWindow(stage: Stage) :
    AbstractWindow(stage, loadLayout("account/SignedOutRoot"), Application.getInstance().windowManager) {

    override val minWidth: Double get() = 500.0
    override val minHeight: Double get() = 400.0
    override val id: String get() = "Account"

    override fun closeAndSaveResources() {
    }
}