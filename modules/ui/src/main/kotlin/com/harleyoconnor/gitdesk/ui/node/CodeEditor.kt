package com.harleyoconnor.gitdesk.ui.node

import com.harleyoconnor.gitdesk.ui.Application
import com.harleyoconnor.gitdesk.util.syntax.SyntaxHighlighter
import com.harleyoconnor.gitdesk.util.system.MacOSManager
import com.harleyoconnor.gitdesk.util.system.SystemManager
import javafx.concurrent.Task
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCombination
import javafx.scene.input.KeyEvent
import org.apache.logging.log4j.LogManager
import org.fxmisc.richtext.CodeArea
import org.fxmisc.richtext.LineNumberFactory
import org.fxmisc.richtext.NavigationActions.SelectionPolicy
import org.fxmisc.richtext.model.StyleSpans
import org.fxmisc.wellbehaved.event.EventPattern.keyPressed
import org.fxmisc.wellbehaved.event.InputMap
import org.fxmisc.wellbehaved.event.InputMap.consume
import org.fxmisc.wellbehaved.event.Nodes
import org.reactfx.Subscription
import java.lang.Character.isWhitespace
import java.time.Duration
import java.util.*
import kotlin.math.min

/**
 * A [CodeArea] implementation with syntax highlighting support built-in. Much of this syntax highlighting is based on
 * [this example](https://github.com/FXMisc/RichTextFX/blob/master/richtextfx-demos/src/main/java/org/fxmisc/richtext/demo/JavaKeywordsAsyncDemo.java).
 *
 * @author Harley O'Connor
 */
class CodeEditor : CodeArea() {

    private lateinit var highlighter: SyntaxHighlighter
    private var highlightingSubscription: Subscription? = null

    init {
        this.paragraphGraphicFactory = LineNumberFactory.get(this)
        this.styleClass.add("code-editor")

        if (SystemManager.get() is MacOSManager) {
            Nodes.addInputMap(this, this.getMacShortcuts())
        }
    }

    fun setupSyntaxHighlighting(highlighter: SyntaxHighlighter) {
        this.highlightingSubscription?.unsubscribe()
        this.highlighter = highlighter
        this.highlightingSubscription = this.multiPlainChanges()
            .successionEnds(Duration.ofMillis(500))
            .retainLatestUntilLater(Application.getInstance().backgroundExecutor)
            .supplyTask(this::computeHighlightingAsync)
            .awaitLatest(this.multiPlainChanges())
            .filterMap { t ->
                if (t.isSuccess) {
                    Optional.of(t.get())
                } else {
                    LogManager.getLogger().error(t.failure)
                    Optional.empty()
                }
            }
            .subscribe(this::applyHighlighting)
    }

    private fun getMacShortcuts(): InputMap<KeyEvent> {
        return InputMap.sequence(
            consume(keyPressed(KeyCode.LEFT, KeyCombination.SHORTCUT_DOWN)) {
                this.lineStart(SelectionPolicy.CLEAR)
            },
            consume(keyPressed(KeyCode.LEFT, KeyCombination.SHORTCUT_DOWN, KeyCombination.SHIFT_DOWN)) {
                this.lineStart(SelectionPolicy.EXTEND)
            },
            consume(keyPressed(KeyCode.RIGHT, KeyCombination.SHORTCUT_DOWN)) {
                this.lineEnd(SelectionPolicy.CLEAR)
            },
            consume(keyPressed(KeyCode.RIGHT, KeyCombination.SHORTCUT_DOWN, KeyCombination.SHIFT_DOWN)) {
                this.lineEnd(SelectionPolicy.EXTEND)
            },

            consume(keyPressed(KeyCode.LEFT, KeyCombination.ALT_DOWN)) {
                this.toPreviousWord(SelectionPolicy.CLEAR)
            },
            consume(keyPressed(KeyCode.LEFT, KeyCombination.ALT_DOWN, KeyCombination.SHIFT_DOWN)) {
                this.toPreviousWord(SelectionPolicy.EXTEND)
            },
            consume(keyPressed(KeyCode.RIGHT, KeyCombination.ALT_DOWN)) {
                this.toNextWord(SelectionPolicy.CLEAR)
            },
            consume(keyPressed(KeyCode.RIGHT, KeyCombination.ALT_DOWN, KeyCombination.SHIFT_DOWN)) {
                this.toNextWord(SelectionPolicy.EXTEND)
            },

            consume(keyPressed(KeyCode.BACK_SPACE, KeyCombination.ALT_DOWN)) { this.deletePreviousWord() },
            consume(keyPressed(KeyCode.DELETE, KeyCombination.ALT_DOWN)) { this.deleteNextWord() },

            consume(keyPressed(KeyCode.BACK_SPACE, KeyCombination.SHORTCUT_DOWN)) { this.deleteLine() },
            consume(keyPressed(KeyCode.DELETE, KeyCombination.SHORTCUT_DOWN)) { this.deleteLine() }
        ).ifConsumed {
            this.requestFollowCaret()
        }
    }

    private fun toPreviousWord(selectionPolicy: SelectionPolicy) {
        if (1 <= caretPosition) {
            val prevCharIsWhiteSpace: Boolean = isWhitespace(this.getText(caretPosition - 1, caretPosition)[0])
            this.wordBreaksBackwards(if (prevCharIsWhiteSpace) 2 else 1, selectionPolicy)
        }
    }

    private fun toNextWord(selectionPolicy: SelectionPolicy) {
        if (caretPosition <= length - 1) {
            val nextCharIsWhiteSpace: Boolean = isWhitespace(this.getText(caretPosition, caretPosition + 1)[0])
            this.wordBreaksForwards(if (nextCharIsWhiteSpace) 2 else 1, selectionPolicy)
        }
    }

    private fun deletePreviousWord() {
        val end: Int = this.caretPosition
        if (end > 0) {
            this.wordBreaksBackwards(2, SelectionPolicy.CLEAR)
            val start: Int = this.caretPosition
            this.replaceText(start, end, "")
        }
    }

    private fun deleteNextWord() {
        val start: Int = this.caretPosition
        if (start < this.length) {
            this.wordBreaksForwards(2, SelectionPolicy.CLEAR)
            val end: Int = this.caretPosition
            this.replaceText(start, end, "")
        }
    }

    private fun deleteLine() {
        val column = this.caretColumn
        this.deleteText(this.currentParagraph, 0, this.currentParagraph + 1, 0)
        this.moveTo(this.currentParagraph, min(this.currentLineEndInParargraph, column))
    }

    private fun computeHighlightingAsync(): Task<StyleSpans<Collection<String>>> {
        val task: Task<StyleSpans<Collection<String>>> = object : Task<StyleSpans<Collection<String>>>() {
            @Suppress("UNCHECKED_CAST")
            @Throws(Exception::class)
            override fun call(): StyleSpans<Collection<String>> {
                return highlighter.highlight(text) as StyleSpans<Collection<String>>
            }
        }
        Application.getInstance().backgroundExecutor.execute(task)
        return task
    }

    private fun applyHighlighting(highlighting: StyleSpans<Collection<String>>) {
        this.setStyleSpans(0, highlighting)
    }

}