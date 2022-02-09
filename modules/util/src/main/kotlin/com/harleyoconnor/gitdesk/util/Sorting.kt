package com.harleyoconnor.gitdesk.util

inline fun <reified T> sort(
    array: Array<T>,
    comparator: Comparator<T>
): Array<T> {
    return mergeSort(array, comparator)
        .stream()
        .map { it }
        .toTypedArray()
}

inline fun <reified T : Comparable<T>> sortComparable(
    array: Array<T>,
    comparator: Comparator<T> = naturalOrder()
): Array<T> {
    return mergeSort(array, comparator)
        .stream()
        .map { it }
        .toTypedArray()
}

fun <T> mergeSort(
    array: Array<T>,
    comparator: Comparator<T>
): Array<T> {
    var split = array.splitInHalf()

    if (split.first.size > 1) {
        split = mergeSort(split.first, comparator) to split.second
    }
    if (split.second.size > 1) {
        split = split.first to mergeSort(split.second, comparator)
    }

    return ComparatorArrayMerger(split.first, split.second, comparator).merge()
}

/**
 * A class that handles merging two arrays together, in some particular order.
 */
private interface ArrayMerger<T> {
    fun merge(): Array<T>
}

/**
 * An [ArrayMerger] that uses the specified [comparator] to determine the order by which to merge elements in the
 * [merge] array.
 */
private class ComparatorArrayMerger<T>(
    first: Array<T>,
    private val second: Array<T>,
    private val comparator: Comparator<T>
) : ArrayMerger<T> {

    private val merged: MutableList<T> = first.toMutableList()
    private var mergedIndex = 0

    @Suppress("UNCHECKED_CAST")
    override fun merge(): Array<T> {
        for (element in second) {
            this.insertElement(element)
        }
        // Converting it to an array in this way prevents Kotlin's default .toList() from somehow turning the array
        // with elements of type T into a list of elements of type Object.
        return merged.stream().toArray { arrayOfNulls<Any>(merged.size) } as Array<T>
    }

    private fun insertElement(element: T) {
        val index = this.findInsertionIndex(element)
        this.merged.add(index, element)
    }

    private fun findInsertionIndex(element: T): Int {
        for (index in mergedIndex until merged.size) {
            if (this.shouldInsertAt(element, index)) {
                mergedIndex++
                return index
            }
        }
        return merged.size
    }

    private fun shouldInsertAt(element: T, index: Int): Boolean {
        return comparator.compare(element, merged[index]) < 0
    }
}
