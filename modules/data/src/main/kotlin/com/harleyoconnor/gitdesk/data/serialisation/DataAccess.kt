package com.harleyoconnor.gitdesk.data.serialisation

/**
 * @author Harley O'Connor
 */
interface DataAccess<K, D> {

    fun get(key: K): D

    fun save(key: K, data: D)

}