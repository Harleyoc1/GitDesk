package com.harleyoconnor.gitdesk.data

import com.harleyoconnor.gitdesk.data.highlighting.SyntaxHighlighterAccess
import com.harleyoconnor.gitdesk.data.local.RepositoryAccess
import com.harleyoconnor.gitdesk.data.serialisation.adapter.addExtraAdapters
import com.harleyoconnor.gitdesk.util.Directory
import com.harleyoconnor.gitdesk.util.create
import com.harleyoconnor.gitdesk.util.makeDirectories
import com.harleyoconnor.gitdesk.util.plus
import com.harleyoconnor.gitdesk.util.system.SystemManager
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.io.File
import java.nio.file.FileSystems
import java.nio.file.Path

val MOSHI: Moshi = Moshi.Builder()
    .addExtraAdapters()
    .addLast(KotlinJsonAdapterFactory())
    .build()

/**
 *
 * @author Harley O'Connor
 */
object Data {

    val repositoryAccess: RepositoryAccess = RepositoryAccess(
        getPath("Repositories.json").toFile().create(),
        Directory(
            getPath("Repositories").toFile()
        ).makeDirectories()
    )

    val syntaxHighlighterAccess: SyntaxHighlighterAccess = SyntaxHighlighterAccess(
        Directory(getPath("Syntax Highlighting").toFile())
    )

    private fun getPath(relativePath: String): Path {
        return getAppDataPath() + (File.separatorChar + relativePath)
    }

    private fun getAppDataPath(): Path {
        return FileSystems.getDefault().getPath(
            SystemManager.get().getAppDataLocation() + File.separatorChar + "GitDesk"
        )
    }

}