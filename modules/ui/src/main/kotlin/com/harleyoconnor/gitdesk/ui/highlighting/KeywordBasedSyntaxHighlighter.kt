package com.harleyoconnor.gitdesk.ui.highlighting

import com.harleyoconnor.gitdesk.data.highlighting.SyntaxHighlighterTypes
import com.harleyoconnor.gitdesk.util.with
import com.squareup.moshi.Json
import java.util.regex.Pattern

class KeywordBasedSyntaxHighlighter(
    priority: Int,
    @Json(name = "name_pattern") fileNamePattern: Pattern,
    private val keywords: Array<String>,
    highlights: Array<Highlight>
) : CodeSyntaxHighlighter(priority, fileNamePattern) {

    companion object {
        fun registerType() {
            SyntaxHighlighterTypes.register("keyword_based", KeywordBasedSyntaxHighlighter::class.java)
        }
    }

    override val highlights: Array<Highlight> = highlights.with(
        Highlight("\\b(" + keywords.joinToString("|") { it } + ")\\b", "KEYWORD", "keyword")
    )

}