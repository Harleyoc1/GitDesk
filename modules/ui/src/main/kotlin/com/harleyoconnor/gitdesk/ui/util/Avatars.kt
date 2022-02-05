package com.harleyoconnor.gitdesk.ui.util

import com.harleyoconnor.gitdesk.data.remote.User
import javafx.scene.image.Image
import javafx.scene.paint.ImagePattern
import javafx.scene.shape.Circle

fun User.createAvatarNode(radius: Double = 12.0): Circle {
    return Circle().also {
        it.styleClass.add("bordered")
        it.radius = radius
        it.fill = ImagePattern(Image(this.avatarUrl.toExternalForm()))
    }
}