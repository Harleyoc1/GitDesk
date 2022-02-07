package com.harleyoconnor.gitdesk.data.settings

import com.squareup.moshi.JsonAdapter
import java.io.File

class JsonSettings<T : Settings.SettingsData>(
    private val dataAdapter: JsonAdapter<T>,
    private val defaultData: T,
    private val dataFile: File
) : Settings<T> {

    override fun getOrLoad(): T {
        return load() ?: defaultData
    }

    private fun load(): T? {
        return dataAdapter.fromJson(
            dataFile.readText()
        )
    }

    override fun save(data: T) {
        dataFile.writeText(
            dataAdapter.toJson(data)
        )
    }

}