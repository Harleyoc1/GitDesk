package com.harleyoconnor.gitdesk.ui.form.selection

import com.harleyoconnor.gitdesk.ui.translation.TRANSLATIONS_BUNDLE

class NoSelection<T>: Selection<T> {
    override fun toString(): String {
        return TRANSLATIONS_BUNDLE.getString("ui.repository.branches.create.field.upstream.none")
    }
}