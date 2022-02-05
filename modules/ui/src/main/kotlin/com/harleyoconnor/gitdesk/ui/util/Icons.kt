package com.harleyoconnor.gitdesk.ui.util

import com.harleyoconnor.gitdesk.ui.UIResource
import com.harleyoconnor.gitdesk.ui.node.SVGIcon
import com.harleyoconnor.gitdesk.util.xml.SVG
import com.harleyoconnor.gitdesk.util.xml.SVGCache

val OPEN_ISSUE_ICON = SVGCache.getOrLoad(UIResource("/ui/icons/issue.svg"))
val CLOSED_ISSUE_ICON = SVGCache.getOrLoad(UIResource("/ui/icons/closed_issue.svg"))

val OPEN_PULL_REQUEST_ICON = SVGCache.getOrLoad(UIResource("/ui/icons/branch.svg"))
val CLOSED_PULL_REQUEST_ICON = SVGCache.getOrLoad(UIResource("/ui/icons/closed_pull_request.svg"))

fun createOpenIssueIcon(): SVGIcon {
    return createOpenIcon(OPEN_ISSUE_ICON)
}

fun createOpenPullRequestIcon(): SVGIcon {
    return createOpenIcon(OPEN_PULL_REQUEST_ICON)
}

fun createOpenIcon(svg: SVG): SVGIcon {
    return SVGIcon().also { icon ->
        icon.setPrefSize(14.0, 14.0)
        icon.setupFromSvg(svg)
        icon.styleClass.addAll("icon", "open-accent")
    }
}

fun createClosedIssueIcon(): SVGIcon {
    return createClosedIcon(CLOSED_ISSUE_ICON)
}

fun createClosedPullRequestIcon(): SVGIcon {
    return createClosedIcon(CLOSED_PULL_REQUEST_ICON).also {
        it.styleClass.add("rejected-accent")
    }
}

fun createClosedIcon(svg: SVG): SVGIcon {
    return SVGIcon().also { icon ->
        icon.setPrefSize(14.0, 14.0)
        icon.setupFromSvg(svg)
        icon.styleClass.addAll("icon", "closed-accent")
    }
}