package com.harleyoconnor.gitdesk.data.serialisation.adapter

import com.harleyoconnor.gitdesk.data.serialisation.util.findNextName
import com.harleyoconnor.gitdesk.util.Directory
import com.harleyoconnor.gitdesk.util.tree.MutableArrayTree
import com.harleyoconnor.gitdesk.util.tree.MutableTree
import com.harleyoconnor.gitdesk.util.tree.Tree
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import java.io.File

/**
 *
 * @author Harley O'Connor
 */
object DirectoryTreeAdapter : JsonAdapter<MutableTree<Directory>>() {

    override fun fromJson(reader: JsonReader): MutableTree<Directory>? {
        if (!reader.hasNext() || reader.peek() == JsonReader.Token.NULL) {
            return null
        }
        return MutableArrayTree(readParentNode(reader, null))
    }

    private fun readParentNode(
        reader: JsonReader,
        parent: MutableArrayTree.ParentNode<Directory>?
    ): MutableArrayTree.ParentNode<Directory> {
        reader.beginObject()
        reader.findNextName()
        val node = MutableArrayTree.ParentNode(
            parent?.get()?.getChild(reader.nextName()) ?: Directory(File(reader.nextName())), parent
        )
        reader.beginArray()
        while (reader.hasNext()) {
            node.addChildNodes(readChildNode(reader, node))
        }
        reader.endArray()
        reader.endObject()
        return node
    }

    private fun readChildNode(
        reader: JsonReader,
        parent: MutableArrayTree.ParentNode<Directory>
    ): Tree.Node<Directory> {
        return when {
            (reader.peek() == JsonReader.Token.STRING) ->
                MutableArrayTree.LeafNode(parent.get().getChild(reader.nextString()), parent)
            (reader.peek() == JsonReader.Token.BEGIN_OBJECT) ->
                readParentNode(reader, parent)
            else -> throw JsonDataException("Unsupported node in directory tree.")
        }
    }

    override fun toJson(writer: JsonWriter, value: MutableTree<Directory>?) {
        if (value == null) {
            writer.nullValue()
            return
        }
        writeParentNode(writer, value.root().get().toString(), value.root())
    }


    private fun writeNode(writer: JsonWriter, node: Tree.Node<Directory>, parent: Tree.ParentNode<Directory>) {
        val relativePath = node.get().toRelativeString(parent.get())
        if (node is Tree.LeafNode) {
            writer.value(relativePath)
        } else if (node is Tree.ParentNode) {
            writeParentNode(writer, relativePath, node)
        }
    }

    private fun writeParentNode(
        writer: JsonWriter,
        relativePath: String,
        node: Tree.ParentNode<Directory>
    ) {
        writer.beginObject()
        writer.name(relativePath)
        writer.beginArray()
        node.getChildNodes().forEach {
            writeNode(writer, it, node)
        }
        writer.endArray()
        writer.endObject()
    }

}