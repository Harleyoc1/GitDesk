package com.harleyoconnor.gitdesk.data.serialisation

/**
 * @author Harley O'Connor
 */
interface Serialiser<D, O> {

    fun serialise(data: D): O

}