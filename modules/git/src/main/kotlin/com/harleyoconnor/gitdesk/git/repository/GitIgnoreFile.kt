package com.harleyoconnor.gitdesk.git.repository

import java.io.File
import java.util.concurrent.Executor

/**
 * Represents the root `.gitignore` file of a Git repository. Objects handle maintaining and modifying its set of rules.
 *
 * @author Harley O'Connor
 */
class GitIgnoreFile(
    private val repository: Repository,
    private val backgroundExecutor: Executor
) : IgnoreFile {

    private val file: File = File(repository.directory.canonicalPath + File.separatorChar + ".gitignore")

    /**
     * Stores the list of raw lines in the ignored file. Indices are equivalent to their equivalent [Rule] objects in
     * the [_rules] list. They are stored to retain the lines order when writing changes to the file.
     */
    private val ruleLines: MutableList<String> by lazy {
        readFile().toMutableList()
    }

    private val _rules: MutableList<IgnoreFile.Rule> by lazy {
        parseRules().toMutableList()
    }

    /**
     * The current list of ignore rules in place.
     */
    override val rules: List<IgnoreFile.Rule> get() = _rules.filter { it != PlaceholderRule }

    override fun add(rule: IgnoreFile.Rule) {
        _rules.add(rule)
        ruleLines.add(rule.body)
        save()
    }

    override fun add(body: String) {
        _rules.add(parseRule(body))
        ruleLines.add(body)
        save()
    }

    override fun edit(rule: IgnoreFile.Rule, newBody: String) {
        val indexOf = _rules.indexOf(rule)
        if (indexOf > -1) {
            _rules[indexOf] = parseRule(newBody)
            ruleLines[indexOf] = newBody
            save()
        }
    }

    override fun remove(rule: IgnoreFile.Rule) {
        val indexOf = _rules.indexOf(rule)
        if (indexOf > -1) {
            _rules.removeAt(indexOf)
            ruleLines.removeAt(indexOf)
            save()
        }
    }

    override fun refresh() {
        ruleLines.clear()
        ruleLines.addAll(readFile())
        _rules.clear()
        _rules.addAll(parseRules())
    }

    private fun readFile(): List<String> {
        if (!file.exists()) {
            file.createNewFile()
        }
        return file.readLines()
    }

    private fun parseRules(): List<IgnoreFile.Rule> {
        return ruleLines.map {
            parseRule(it)
        }
    }

    private fun parseRule(rule: String): IgnoreFile.Rule {
        val body = rule.trim() // Remove any trailing and leading whitespace.
        if (isNotRule(body)) {
            return PlaceholderRule
        }
        return if (IgnoreFile.DirectoryRule.isOfType(body)) {
            IgnoreFile.DirectoryRule(body)
        } else if (IgnoreFile.FileExtensionRule.isOfType(body)) {
            IgnoreFile.FileExtensionRule(body)
        } else IgnoreFile.CustomRule(body)
    }

    private fun isNotRule(rule: String) = rule.isBlank() || rule.startsWith('#')

    private fun save() {
        if (!file.exists()) {
            file.createNewFile()
        }
        file.writeText(ruleLines.joinToString("\n"))
        repository.updateTrackedFiles(backgroundExecutor)
    }

    private object PlaceholderRule : IgnoreFile.Rule {
        override val body: String = ""
    }

}