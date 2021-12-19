package com.harleyoconnor.gitdesk.data.serialisation

import com.harleyoconnor.gitdesk.data.MOSHI
import com.squareup.moshi.JsonAdapter

/**
 *
 * @author Harley O'Connor
 */
class Typed(
    val type: String
) {

    companion object {
        val ADAPTER: JsonAdapter<Typed> by lazy { MOSHI.adapter(Typed::class.java) }
    }

}