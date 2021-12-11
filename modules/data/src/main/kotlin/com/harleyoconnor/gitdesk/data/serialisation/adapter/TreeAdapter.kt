package com.harleyoconnor.gitdesk.data.serialisation.adapter

import com.harleyoconnor.gitdesk.util.tree.Tree
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonAdapter.Factory
import com.squareup.moshi.JsonReader
import com.squareup.moshi.Types
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * @author Harley O'Connor
 */
class TreeAdapter<T>(nodeAdapter: JsonAdapter<T>) : MutableTreeAdapter<T>(nodeAdapter) {

    companion object {
        val FACTORY = Factory { type, annotations, moshi ->
            if (annotations.isNotEmpty()) return@Factory null
            val rawType = Types.getRawType(type)
            if (rawType != Tree::class.java) return@Factory null
            val supertype: Type = getSupertype(type, rawType)
            TreeAdapter<Any>(moshi.adapter((supertype as ParameterizedType).actualTypeArguments[0])).nullSafe()
        }
    }

    override fun readTree(reader: JsonReader): Tree<T> {
        return super.readTree(reader).immutable()
    }

}