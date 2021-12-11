package com.harleyoconnor.gitdesk.git.repository

import com.harleyoconnor.gitdesk.git.gitCommand
import com.harleyoconnor.gitdesk.util.process.FunctionalProcessBuilder

/**
 * @author Harley O'Connor
 */
class Remote(
    private val repository: Repository,
    val name: String
) {

    fun getUrl(): String {
        return FunctionalProcessBuilder.normal()
            .gitCommand()
            .arguments("config", "--get", "remote.$name.url")
            .directory(repository.directory)
            .beginAndWaitFor().result!!
    }

}