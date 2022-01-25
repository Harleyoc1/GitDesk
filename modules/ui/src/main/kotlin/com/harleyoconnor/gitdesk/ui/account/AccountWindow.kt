package com.harleyoconnor.gitdesk.ui.account

import com.harleyoconnor.gitdesk.data.account.Account
import com.harleyoconnor.gitdesk.data.account.Session
import com.harleyoconnor.gitdesk.ui.Application
import com.harleyoconnor.gitdesk.ui.window.AbstractWindow
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

    private val signedOutView by lazy {
        SignedOutController.Loader.load(SignedOutController.Context(this))
    }

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
        root = SignedInController.Loader.load(SignedInController.Context(this, account)).root
    }

    fun toSignedOutView() {
        root = signedOutView.root
    }

    override fun closeAndSaveResources() {
    }
}