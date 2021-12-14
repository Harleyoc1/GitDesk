package com.harleyoconnor.gitdesk.ui.node

import com.harleyoconnor.gitdesk.ui.UIResource
import com.harleyoconnor.gitdesk.util.xml.SVG
import com.harleyoconnor.gitdesk.util.xml.SVGCache
import javafx.fxml.FXML
import javafx.scene.layout.Region
import javafx.scene.shape.SVGPath

/**
 * @author Harley O'Connor
 */
class SVGIcon : Region() {

    @FXML
    private lateinit var resourcePath: String

    @FXML
    fun getPath(): String {
        return resourcePath
    }

    @FXML
    fun setPath(path: String) {
        this.resourcePath = path
        val svg = SVGCache.getOrLoad(UIResource(path))
        setupFromSvg(svg)
    }

    fun setupFromSvg(svg: SVG) {
        val path = SVGPath()
        path.content = svg.path
        shape = path
        isScaleShape = true
        scaleX = prefWidth / svg.width
        scaleY = prefHeight / svg.height
        isScaleShape = false
    }

}