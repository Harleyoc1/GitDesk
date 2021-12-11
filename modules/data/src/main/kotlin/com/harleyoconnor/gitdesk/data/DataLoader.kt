package com.harleyoconnor.gitdesk.data

/**
 * @author Harley O'Connor
 */
interface DataLoader<T> {

    fun load(): T

    fun save(data: T)

}