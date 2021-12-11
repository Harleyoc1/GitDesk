package com.harleyoconnor.gitdesk.util.xml

import org.w3c.dom.Node
import org.w3c.dom.NodeList
import java.util.Arrays
import java.util.stream.Stream

fun NodeList.forEach(action: (Node) -> Unit) {
    for (i in 0 until this.length) {
        action.invoke(this.item(i))
    }
}

@Suppress("UNCHECKED_CAST")
fun <N : Node> NodeList.forEachType(nodeType: NodeType<N>, action: (N) -> Unit) {
    for (i in 0 until this.length) {
        val node = this.item(i)
        if (nodeType.isThis(node.nodeType)) {
            action.invoke(node as N)
        }
    }
}

fun NodeList.toArray(): Array<Node> {
    val array = arrayOfNulls<Node>(this.length)
    for (i in 0 until this.length) {
        array[i] = this.item(i)
    }
    return array.requireNoNulls()
}

fun NodeList.stream(): Stream<Node> {
    return Arrays.stream(this.toArray())
}

fun NodeList.typeCount(nodeType: NodeType<*>): Long {
    return this.stream()
        .filter { nodeType.isThis(it.nodeType) }
        .count()
}
