package com.harleyoconnor.gitdesk.util.xml

import org.w3c.dom.Attr
import org.w3c.dom.CDATASection
import org.w3c.dom.Comment
import org.w3c.dom.Document
import org.w3c.dom.DocumentFragment
import org.w3c.dom.DocumentType
import org.w3c.dom.Element
import org.w3c.dom.Entity
import org.w3c.dom.EntityReference
import org.w3c.dom.Node
import org.w3c.dom.Notation
import org.w3c.dom.ProcessingInstruction
import org.w3c.dom.Text

/**
 * @author Harley O'Connor
 */
sealed class NodeType<N : Node>(
    private val id: Short,
    val type: Class<N>
) {
    object ELEMENT : NodeType<Element>(Node.ELEMENT_NODE, Element::class.java)
    object ATTRIBUTE : NodeType<Attr>(Node.ATTRIBUTE_NODE, Attr::class.java)
    object TEXT : NodeType<Text>(Node.TEXT_NODE, Text::class.java)
    object CDATA_SECTION : NodeType<CDATASection>(Node.CDATA_SECTION_NODE, CDATASection::class.java)
    object ENTITY_REFERENCE : NodeType<EntityReference>(Node.ENTITY_REFERENCE_NODE, EntityReference::class.java)
    object ENTITY : NodeType<Entity>(Node.ENTITY_NODE, Entity::class.java)
    object PROCESSING_INSTRUCTION :
        NodeType<ProcessingInstruction>(Node.PROCESSING_INSTRUCTION_NODE, ProcessingInstruction::class.java)

    object COMMENT : NodeType<Comment>(Node.COMMENT_NODE, Comment::class.java)
    object DOCUMENT : NodeType<Document>(Node.DOCUMENT_NODE, Document::class.java)
    object DOCUMENT_TYPE : NodeType<DocumentType>(Node.DOCUMENT_TYPE_NODE, DocumentType::class.java)
    object DOCUMENT_FRAGMENT : NodeType<DocumentFragment>(Node.DOCUMENT_FRAGMENT_NODE, DocumentFragment::class.java)
    object NOTATION : NodeType<Notation>(Node.NOTATION_NODE, Notation::class.java)

    fun isThis(id: Short): Boolean {
        return this.id == id
    }
}