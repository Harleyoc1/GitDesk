package com.harleyoconnor.gitdesk.git.repository

import com.harleyoconnor.gitdesk.git.gitCommand
import com.harleyoconnor.gitdesk.util.Directory
import com.harleyoconnor.gitdesk.util.process.FunctionalProcessBuilder
import java.net.URL
import java.util.regex.Pattern

/**
 * @author Harley O'Connor
 */
interface Remote {

    companion object {
        private val types = mutableListOf<Type>()

        fun registerType(urlPattern: Pattern, constructor: (URL) -> Remote?) {
            types.add(Type(urlPattern, constructor))
        }

        fun getRemote(url: URL): Remote {
            for (it in this.types) {
                if (it.urlPattern.matcher(url.toExternalForm()).matches()) {
                    return it.constructor(url) ?: createRawRemote(url)
                }
            }
            return createRawRemote(url)
        }

        private fun createRawRemote(url: URL): Remote = object : Remote {
            override val url: URL = url
        }

        fun getUpstreamName(gitDirectory: Directory, branchName: String): String = FunctionalProcessBuilder.normal()
            .gitCommand()
            .arguments("config", "--get", "branch.$branchName.remote")
            .directory(gitDirectory)
            .beginAndWaitFor().result!!

        fun getUrl(gitDirectory: Directory, remoteName: String): URL = URL(
            FunctionalProcessBuilder.normal()
                .gitCommand()
                .arguments("config", "--get", "remote.$remoteName.url")
                .directory(gitDirectory)
                .beginAndWaitFor().result!!
        )

        private class Type(
            val urlPattern: Pattern,
            val constructor: (URL) -> Remote?
        )
    }

    val url: URL

}