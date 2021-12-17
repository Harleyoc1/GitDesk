package com.harleyoconnor.gitdesk.data.serialisation

import com.squareup.moshi.JsonDataException
import kotlin.jvm.Throws

/**
 * @author Harley O'Connor
 */
interface Deserialiser<K, D> {

    @Throws(JsonDataException::class)
    fun deserialise(key: K): D

}