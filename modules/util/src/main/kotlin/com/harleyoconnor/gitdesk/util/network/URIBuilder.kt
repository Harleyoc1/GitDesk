package com.harleyoconnor.gitdesk.util.network

import java.net.URI

/**
 * @author Harley O'Connor
 */
class URIBuilder {

    private var uriStr = StringBuilder()

    fun ftp(): URIBuilder {
        return this.insert(0, "ftp://")
    }

    fun sftp(): URIBuilder {
        return this.insert(0, "sftp://")
    }

    @Suppress("HttpUrlsUsage")
    fun http(): URIBuilder {
        return this.insert(0, "http://")
    }

    fun https(): URIBuilder {
        return this.insert(0, "https://")
    }

    fun append(str: String): URIBuilder {
        this.uriStr.append(str.replace(" ", "%20"))
        return this
    }

    fun insert(index: Int, str: String): URIBuilder {
        this.uriStr.insert(index, str)
        return this
    }

    fun parameter(key: String, value: String): URIBuilder {
        val containsQuestion = this.uriStr.contains("?")
        val endsWithAnd = this.uriStr.endsWith("&")

        if (!this.uriStr.endsWith("?") && !endsWithAnd && !containsQuestion) {
            this.uriStr.append("?")
        } else if (!endsWithAnd && containsQuestion) {
            this.uriStr.append("&")
        }

        return this.append("$key=$value")
    }

    fun parameters(parameters: Map<String, String>): URIBuilder {
        parameters.forEach { (key, value) ->
            parameter(key, value)
        }
        return this
    }

    fun build(): URI {
        return URI.create(this.uriStr.toString())
    }

    override fun toString(): String {
        return this.build().toString()
    }

}