package com.harleyoconnor.gitdesk.util

import java.io.File
import java.util.Arrays
import java.util.stream.Stream

fun File.stream(): Stream<File> {
    return Arrays.stream(this.listFiles())
}

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