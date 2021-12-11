package com.harleyoconnor.gitdesk.util.xml

import com.harleyoconnor.gitdesk.util.Resource
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.NodeList
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory

/**
 * An SVG in terms of its [path] and native [width] and [height].
 *
 * @author Harley O'Connor
 */
data class SVG(
    val path: String,
    val width: Double,
    val height: Double
) {

    companion object {
        private val FACTORY: DocumentBuilderFactory by lazy { DocumentBuilderFactory.newInstance() }
        private val BUILDER: DocumentBuilder by lazy { FACTORY.newDocumentBuilder() }

        /**
         * @return a deserialised [SVG] from the [resource] contents
         */
        fun from(resource: Resource): SVG {
            var path = ""
            var width = 0.0
            var height = 0.0
            val svg = parseSvg(resource)

            val svgNodes = svg.getElementsByTagName("svg")

            assertTypeExists(NodeType.ELEMENT, svgNodes, "svg", resource)
            svgNodes.forEachType(NodeType.ELEMENT) { svgElement ->
                val pathNodes = svgElement.getElementsByTagName("path")

                assertTypeExists(NodeType.ELEMENT, pathNodes, "path", resource)
                pathNodes.forEachType(NodeType.ELEMENT) {
                    path = getPathOrThrow(it, resource)
                }

                width = svgElement.getAttribute("width").toDoubleOrNull() ?: 24.0
                height = svgElement.getAttribute("height").toDoubleOrNull() ?: 24.0
            }

            return SVG(path, width, height)
        }

        private fun assertTypeExists(type: NodeType<*>, nodes: NodeList, tagName: String, resource: Resource) {
            if (nodes.typeCount(type) < 1) {
                throw XMLDeserialisationException(
                    "Exception deserialising SVG from resource `${resource.path}`: `<$tagName>` tag of type " +
                            "element did not exist in `<svg>` tag."
                )
            }
        }

        private fun getPathOrThrow(element: Element, resource: Resource): String {
            return element.getAttribute("d") ?: throw XMLDeserialisationException(
                "Exception deserialising SVG from resource `${resource.path}`: `d` attribute did not exist in `<path>` tag."
            )
        }

        private fun parseSvg(resource: Resource): Document {
            val svg = BUILDER.parse(resource.stream)
            svg.documentElement.normalize()
            return svg
        }
    }

}