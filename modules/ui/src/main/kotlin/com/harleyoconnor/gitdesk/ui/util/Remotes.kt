package com.harleyoconnor.gitdesk.ui.util

import com.harleyoconnor.gitdesk.data.remote.github.GitHubRemoteRepository
import com.harleyoconnor.gitdesk.git.repository.Remote
import com.harleyoconnor.gitdesk.ui.UIResource
import com.harleyoconnor.gitdesk.util.xml.SVG
import com.harleyoconnor.gitdesk.util.xml.SVGCache

fun Remote.getIcon(): SVG {
    return SVGCache.getOrLoad(
        UIResource( if (this is GitHubRemoteRepository) "/ui/icons/github.svg" else "/ui/icons/web.svg")
    )
}