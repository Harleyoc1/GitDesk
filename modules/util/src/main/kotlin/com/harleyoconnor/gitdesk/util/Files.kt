package com.harleyoconnor.gitdesk.util

import java.io.File

fun File.create(): File {
    this.createNewFile()
    return this
}

fun File.makeDirectories(): File {
    this.mkdirs()
    return this
}

fun Directory.makeDirectories(): Directory {
    this.mkdirs()
    return this
}