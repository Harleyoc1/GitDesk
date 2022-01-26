package com.harleyoconnor.gitdesk.ui.util

import com.harleyoconnor.gitdesk.data.remote.Platform
import com.harleyoconnor.gitdesk.data.remote.github.GitHubRemoteRepository
import com.harleyoconnor.gitdesk.git.repository.Remote
import com.harleyoconnor.gitdesk.ui.UIResource
import com.harleyoconnor.gitdesk.util.xml.SVG
import com.harleyoconnor.gitdesk.util.xml.SVGCache

private val GITHUB_ICON by lazy { SVGCache.getOrLoad(UIResource("/ui/icons/github.svg")) }
private val WEB_ICON by lazy { SVGCache.getOrLoad(UIResource("/ui/icons/web.svg")) }

fun Remote.getIcon(): SVG {
    return if (this is GitHubRemoteRepository) GITHUB_ICON else WEB_ICON
}

fun Platform.getIcon(): SVG {
    return if (this == Platform.GITHUB) GITHUB_ICON else WEB_ICON
}