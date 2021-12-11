package com.harleyoconnor.gitdesk.util.xml

import com.harleyoconnor.gitdesk.util.Resource

/**
 * @author Harley O'Connor
 */
object SVGCache {

    private val pathsToSvgs: MutableMap<String, SVG> = mutableMapOf()

    fun getOrLoad(resource: Resource): SVG {
        return pathsToSvgs[resource.path] ?: load(resource)
    }

    private fun load(resource: Resource): SVG {
        val svg = SVG.from(resource)
        pathsToSvgs[resource.path] = svg
        return svg
    }

}