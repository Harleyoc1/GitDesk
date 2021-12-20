package com.harleyoconnor.gitdesk.ui.account.register

import com.harleyoconnor.gitdesk.data.account.Account
import com.harleyoconnor.gitdesk.data.account.VerificationData
import com.harleyoconnor.gitdesk.ui.util.Tab
import javafx.scene.Node
import javafx.scene.layout.Region
import java.util.function.Consumer

/**
 * @author Harley O'Connor
 */
class RegisterTab(
    private val openSignedInViewCallback: Consumer<Account>,
    setter: (Node) -> Unit
) : Tab(Region(), setter) {

    private val rootView = RegisterController.load(this)

    init {
        this.node = rootView.root
    }

    fun toEmailVerification(verificationData: VerificationData) {
        this.node = VerifyEmailController.load(this, verificationData)
    }

    fun toRoot() {
        this.node = rootView.root
    }

    fun toRootAndClear() {
        rootView.controller.clearAllFields()
        this.node = rootView.root
    }

    fun toSignedInView(account: Account) {
        openSignedInViewCallback.accept(account)
    }

}