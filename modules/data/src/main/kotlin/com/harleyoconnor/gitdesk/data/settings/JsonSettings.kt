package com.harleyoconnor.gitdesk.data.settings

import com.harleyoconnor.gitdesk.util.create
import com.squareup.moshi.JsonAdapter
import java.io.EOFException
import java.io.File

class JsonSettings<T : Settings.SettingsData>(
    private val dataAdapter: JsonAdapter<T>,
    private val defaultData: T,
    private val dataFile: File
) : Settings<T> {

    override fun getOrLoad(): T {
        return load() ?: defaultData.also {
            it.onLoad()
        }
    }

    private fun load(): T? {
        return try {
            dataAdapter.fromJson(
                dataFile.create().readText()
            ).also {
                it?.onLoad()
            }
        } catch (e: EOFException) {
            null
        }
    }

    override fun save(data: T) {
        data.onSave()
        dataFile.create().writeText(
            dataAdapter.toJson(data)
        )
    }

}