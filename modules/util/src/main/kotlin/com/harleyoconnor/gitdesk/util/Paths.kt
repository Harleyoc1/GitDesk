package com.harleyoconnor.gitdesk.util

import java.nio.file.Path

operator fun Path.plus(relativePathName: String): Path {
    return Path.of(this.toString(), relativePathName)
}
