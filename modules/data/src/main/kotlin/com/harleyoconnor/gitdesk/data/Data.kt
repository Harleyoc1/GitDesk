package com.harleyoconnor.gitdesk.data

import com.harleyoconnor.gitdesk.data.repository.RepositoryAccess
import com.harleyoconnor.gitdesk.data.repository.RepositoryHolder
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

/**
 *
 * @author Harley O'Connor
 */
object Data {

    val moshi: Moshi = Moshi.Builder()
        .addExtraAdapters()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    val repositoryAccess: RepositoryAccess = RepositoryHolder(
        getPath("Repositories.json").toFile().create(),
        Directory(getPath("Repositories").toFile().makeDirectories())
    )

//    val syntaxHighlighterAccess: SyntaxHighlighterAccess = SyntaxHighlighterHolder(
//        getPath("Syntax Highlighting")
//    )

    init {
        repositoryAccess.load()
//        syntaxHighlighterAccess.load()
    }

    private fun getPath(relativePath: String): Path {
        return getAppDataPath() + relativePath
    }

    private fun getAppDataPath(): Path {
        return FileSystems.getDefault().getPath(
            SystemManager.get().getAppDataLocation() + File.separatorChar + "GitDesk"
        )
    }

    fun saveAllData() {
        repositoryAccess.save()
    }

}