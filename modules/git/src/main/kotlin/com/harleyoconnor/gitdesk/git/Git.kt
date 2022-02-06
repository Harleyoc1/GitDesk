package com.harleyoconnor.gitdesk.git

import com.harleyoconnor.gitdesk.git.repository.Repository
import com.harleyoconnor.gitdesk.util.Directory
import com.harleyoconnor.gitdesk.util.process.FunctionalProcessBuilder
import com.harleyoconnor.gitdesk.util.process.ProceduralProcessBuilder
import com.harleyoconnor.gitdesk.util.process.ProcessBuilder
import com.harleyoconnor.gitdesk.util.process.Response
import java.net.URL

fun <B : ProcessBuilder<R>, R : Response> B.gitCommand(): B {
    this.command("git")
    return this
}

fun initRepository(directory: Directory): Boolean {
    return ProceduralProcessBuilder()
        .gitCommand()
        .directory(directory)
        .arguments("init")
        .beginAndWaitFor().success()
}

fun repositoryExistsAt(directory: Directory): Boolean {
    return ProceduralProcessBuilder()
        .gitCommand()
        .directory(directory)
        .arguments("status")
        .beginAndWaitFor().success()
}

fun repositoryExistsAt(url: URL): Boolean {
    return ProceduralProcessBuilder()
        .gitCommand()
        .arguments("ls-remote", url.toString())
        .beginAndWaitFor().success()
}

fun clone(from: URL, to: Directory): FunctionalProcessBuilder<Repository> {
    return FunctionalProcessBuilder {
        Repository(to)
    }.gitCommand()
        .arguments("clone", from.toString(), to.absolutePath)
}

fun clone(from: URL, to: Directory, credentials: Credentials): FunctionalProcessBuilder<Repository> {
    val fromUrl = credentials.addToUrl(from)
    return FunctionalProcessBuilder {
        Repository(to)
    }.gitCommand()
        .arguments("clone", fromUrl.toString(), to.absolutePath)
}