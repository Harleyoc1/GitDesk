package com.harleyoconnor.gitdesk.data.settings

interface Settings<T> {

    fun getOrLoad(): T

    fun save(data: T)

    interface SettingsData {
        fun onLoad()
        fun onSave()
    }

}