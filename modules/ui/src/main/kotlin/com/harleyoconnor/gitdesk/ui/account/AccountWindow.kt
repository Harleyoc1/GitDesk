package com.harleyoconnor.gitdesk.ui.account

import com.harleyoconnor.gitdesk.data.account.Account
import com.harleyoconnor.gitdesk.data.account.Session
import com.harleyoconnor.gitdesk.ui.Application
import com.harleyoconnor.gitdesk.ui.window.AbstractWindow
import javafx.scene.layout.BorderPane
import javafx.scene.layout.Region
import javafx.stage.Stage

/**
 *
 * @author Harley O'Connor
 */
class AccountWindow(stage: Stage) :
    AbstractWindow(stage, Region(), Application.getInstance().windowManager) {

    override val minWidth: Double get() = 500.0
    override val minHeight: Double get() = 400.0
    override val id: String get() = "Account"

    private val signedOutView: BorderPane by lazy { SignedOutController.load(this) }

    init {
        val session = Session.load()
        val account = session?.getAccount()
        if (session != null && account != null) {
            toSignedInView(account)
        } else {
            toSignedOutView()
        }
    }

    fun toSignedInView(account: Account) {
        root = SignedInController.load(this, account)
    }

    fun toSignedOutView() {
        root = signedOutView
    }

    override fun closeAndSaveResources() {
    }
}