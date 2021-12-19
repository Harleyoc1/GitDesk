package com.harleyoconnor.gitdesk.ui.highlighting

import com.harleyoconnor.gitdesk.util.stream
import com.harleyoconnor.gitdesk.util.syntax.SyntaxHighlighter
import com.squareup.moshi.Json
import org.fxmisc.richtext.model.StyleSpans
import org.fxmisc.richtext.model.StyleSpansBuilder
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 *
 * @author Harley O'Connor
 */
abstract class CodeSyntaxHighlighter(
    override val priority: Int,
    protected val fileNamePattern: Pattern
) : SyntaxHighlighter {

    abstract val highlights: Array<Highlight>

    private val pattern: Pattern by lazy {
        Pattern.compile(this.highlights.joinToString("|") {
            it.groupDefinition
        })
    }

    override fun shouldHighlightFor(fileName: String): Boolean {
        return fileNamePattern.matcher(fileName).matches()
    }

    override fun highlight(text: String): StyleSpans<Collection<String>> {
        val matcher: Matcher = this.pattern.matcher(text)
        var lastKwEnd = 0
        val builder = StyleSpansBuilder<Collection<String>>()
        while (matcher.find()) {
            val styleClass = this.findStyleClass(matcher)
            builder.add(Collections.emptyList(), matcher.start() - lastKwEnd)
            builder.add(Collections.singleton(styleClass), matcher.end() - matcher.start())
            lastKwEnd = matcher.end()
        }
        builder.add(Collections.emptyList(), text.length - lastKwEnd)
        return builder.create()
    }

    private fun findStyleClass(matcher: Matcher) = highlights.stream()
        .filter { matcher.group(it.groupName) != null }
        .map { it.styleClass }
        .findFirst()
        .orElseThrow()

    override fun compareTo(other: SyntaxHighlighter): Int {
        return this.priority - other.priority
    }

    class Highlight(
        val pattern: String,
        @Json(name = "group_name") val groupName: String,
        @Json(name = "style_class") val styleClass: String
    ) {
        val groupDefinition by lazy { "(?<${groupName}>${pattern})" }
    }

}