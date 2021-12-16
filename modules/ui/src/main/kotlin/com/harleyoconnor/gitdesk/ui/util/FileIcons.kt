package com.harleyoconnor.gitdesk.ui.util

import com.harleyoconnor.gitdesk.ui.UIResource
import com.harleyoconnor.gitdesk.util.xml.SVG
import com.harleyoconnor.gitdesk.util.xml.SVGCache
import java.io.File

/**
 * @author Harley O'Connor
 */
object FileIcons {

    val `c#`: Lazy<SVG> = lazy { loadIcon("c#") }
    val java: Lazy<SVG> = lazy { loadIcon("java") }
    val kotlin: Lazy<SVG> = lazy { loadIcon("kotlin") }
    val swift: Lazy<SVG> = lazy { loadIcon("swift") }

    val file: SVG by lazy { SVGCache.getOrLoad(UIResource("/ui/icons/file.svg")) }
    val directory: SVG by lazy { SVGCache.getOrLoad(UIResource("/ui/icons/directory.svg")) }

    private val extensionsToIcons: Map<String, Lazy<SVG>> = mapOf(
        "cs" to `c#`, "csx" to `c#`,
        "java" to java,
        "kt" to kotlin, "kts" to kotlin, "ktm" to kotlin,
        "swift" to swift
    )

    fun getIconFor(file: File): SVG {
        return extensionsToIcons[file.name.substringAfterLast(".")]?.value ?: this.file
    }

    private fun loadIcon(name: String): SVG {
        return SVGCache.getOrLoad(UIResource("/ui/icons/languages/$name.svg"))
    }

}

fun File.getIcon(): SVG {
    return if (isDirectory) {
        FileIcons.directory
    } else FileIcons.getIconFor(this)
}