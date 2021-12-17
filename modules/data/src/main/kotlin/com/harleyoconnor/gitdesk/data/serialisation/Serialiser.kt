package com.harleyoconnor.gitdesk.data.serialisation

/**
 * @author Harley O'Connor
 */
interface Serialiser<K, D> {

    fun serialise(key: K, data: D)

}