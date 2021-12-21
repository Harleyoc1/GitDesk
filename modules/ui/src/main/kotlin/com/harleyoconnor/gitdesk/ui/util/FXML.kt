package com.harleyoconnor.gitdesk.ui.util

import com.harleyoconnor.gitdesk.ui.Application
import com.harleyoconnor.gitdesk.ui.translation.TRANSLATIONS_BUNDLE
import javafx.fxml.FXMLLoader
import javafx.scene.Node

fun loaderForLayout(path: String): FXMLLoader =
    FXMLLoader(Application::class.java.getResource("/ui/layouts/$path.fxml"), TRANSLATIONS_BUNDLE)

fun <N : Node> loadLayout(path: String): N =
    FXMLLoader.load(Application::class.java.getResource("/ui/layouts/$path.fxml"), TRANSLATIONS_BUNDLE)

fun <N, C> load(path: String): FXML<N, C> {
    val loader = loaderForLayout(path)
    return FXML(loader.load(), loader.getController())
}

class FXML<N, C>(val root: N, val controller: C)