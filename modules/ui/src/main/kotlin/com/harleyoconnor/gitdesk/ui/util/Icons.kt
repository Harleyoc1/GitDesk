package com.harleyoconnor.gitdesk.ui.util

import com.harleyoconnor.gitdesk.ui.UIResource
import com.harleyoconnor.gitdesk.ui.node.SVGIcon
import com.harleyoconnor.gitdesk.util.xml.SVGCache

val OPEN_ICON = SVGCache.getOrLoad(UIResource("/ui/icons/issue.svg"))
val CLOSED_ICON = SVGCache.getOrLoad(UIResource("/ui/icons/closed_issue.svg"))

fun getCloseIcon(): SVGIcon {
    val icon = SVGIcon()
    icon.setPrefSize(12.0, 12.0)
    icon.setupFromSvg(CLOSED_ICON)
    icon.styleClass.addAll("icon", "closed-accent")
    return icon
}

fun getOpenIcon(): SVGIcon {
    val icon = SVGIcon()
    icon.setPrefSize(12.0, 12.0)
    icon.setupFromSvg(OPEN_ICON)
    icon.styleClass.addAll("icon", "open-accent")
    return icon
}
