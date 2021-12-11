package com.harleyoconnor.gitdesk.data.serialisation.adapter

import com.harleyoconnor.gitdesk.data.serialisation.util.findName
import com.harleyoconnor.gitdesk.data.serialisation.util.findNameOrElse
import com.harleyoconnor.gitdesk.util.tree.MutableArrayTree
import com.harleyoconnor.gitdesk.util.tree.MutableTree
import com.harleyoconnor.gitdesk.util.tree.Tree
import com.harleyoconnor.javautilities.reflect.Reflect
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonAdapter.Factory
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.Types
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * @author Harley O'Connor
 */
open class MutableTreeAdapter<T>(
    private val nodeAdapter: JsonAdapter<T>
) : JsonAdapter<Tree<T>>() {

    companion object {
        val FACTORY = Factory { type, annotations, moshi ->
            if (annotations.isNotEmpty()) return@Factory null
            val rawType = Types.getRawType(type)
            if (rawType != MutableTree::class.java) return@Factory null
            val supertype: Type = getSupertype(type, rawType)
            MutableTreeAdapter<Any>(moshi.adapter((supertype as ParameterizedType).actualTypeArguments[0])).nullSafe()
        }

        internal fun getSupertype(type: Type, rawType: Class<*>?): Type =
            Reflect.uncheckedOn(Types::class.java)
                .invoke(
                    "getSupertype",
                    listOf(Type::class.java, Class::class.java, Class::class.java),
                    listOf(type, rawType, Tree::class.java)
                )
    }

    override fun fromJson(reader: JsonReader): Tree<T>? {
        if (reader.peek() == JsonReader.Token.NULL) {
            return null
        }
        return this.readTree(reader)
    }

    protected open fun readTree(reader: JsonReader): Tree<T> {
        val root = this.readNode(reader, null)
        if (root !is MutableArrayTree.ParentNode) {
            throw JsonDataException("Root element must be parent node.")
        }
        return MutableArrayTree(root)
    }

    private fun readNode(reader: JsonReader, parent: MutableArrayTree.ParentNode<T>?): Tree.Node<T> {
        reader.beginObject()
        reader.findName("value")
        val value = this.nodeAdapter.fromJson(reader)!!
        reader.findNameOrElse("children") {
            reader.endObject()
            return MutableArrayTree.LeafNode(value, parent!!)
        }
        val node = MutableArrayTree.ParentNode(value, parent)
        node.addChildNodes(*this.readChildren(reader, node))
        reader.endObject()
        return node
    }

    private fun readChildren(reader: JsonReader, parent: MutableArrayTree.ParentNode<T>): Array<Tree.Node<T>> {
        val nodes = mutableListOf<Tree.Node<T>>()
        reader.beginArray()
        while (reader.hasNext()) {
            nodes.add(this.readNode(reader, parent))
        }
        reader.endArray()
        return nodes.toTypedArray()
    }

    override fun toJson(writer: JsonWriter, value: Tree<T>?) {
        if (value == null) {
            writer.nullValue()
        } else {
            this.writeParentNode(writer, value.root())
        }
    }

    private fun writeParentNode(
        writer: JsonWriter,
        root: Tree.ParentNode<T>
    ) {
        writer.beginObject()
        writer.name("value")
        this.nodeAdapter.toJson(writer, root.get())
        this.writeChildren(writer, root)
        writer.endObject()
    }

    private fun writeChildren(
        writer: JsonWriter,
        node: Tree.ParentNode<T>
    ) {
        writer.name("children")
        writer.beginArray()
        node.getChildNodes().forEach {
            this.writeNode(it, writer)
        }
        writer.endArray()
    }

    private fun writeNode(it: Tree.Node<T>, writer: JsonWriter) {
        if (it is Tree.ParentNode) {
            this.writeParentNode(writer, it)
        } else {
            this.writeLeafNode(writer, it)
        }
    }

    private fun writeLeafNode(writer: JsonWriter, it: Tree.Node<T>) {
        writer.beginObject()
        writer.name("value")
        this.nodeAdapter.toJson(writer, it.get())
        writer.endObject()
    }

}