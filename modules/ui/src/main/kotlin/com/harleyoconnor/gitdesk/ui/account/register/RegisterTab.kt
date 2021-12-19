package com.harleyoconnor.gitdesk.ui.account.register

import com.harleyoconnor.gitdesk.data.account.VerificationData
import com.harleyoconnor.gitdesk.ui.util.Tab
import javafx.scene.Node
import javafx.scene.layout.Region

/**
 * @author Harley O'Connor
 */
class RegisterTab(
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

}