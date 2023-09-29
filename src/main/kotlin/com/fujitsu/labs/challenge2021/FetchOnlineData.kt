package com.fujitsu.labs.challenge2021

import org.wikidata.wdtk.datamodel.interfaces.ItemDocument
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue
import org.wikidata.wdtk.datamodel.interfaces.StatementDocument
import org.wikidata.wdtk.wikibaseapi.WikibaseDataFetcher

/**
 * The main function that fetches online data and prints class definition.
 */
fun main() {
    val aa = FetchOnlineData()
    val tt = aa.getType("Douglas Adams")
    println("a fjs:$tt .")
    aa.printClassDefinition(tt)
}

/**
 * A class to fetch online data from Wikibase.
 */
class FetchOnlineData {

    private val wbdf: WikibaseDataFetcher = WikibaseDataFetcher.getWikidataDataFetcher()

    /**
     * Gets the ID of an entity by its name.
     * @param str The name of the entity.
     * @return The ID of the entity.
     */
    fun getId(str: String): String {
        val q43 = wbdf.searchEntities(str, "en", 10)
        return if (q43.isEmpty()) { "" } else { q43.first().entityId }
    }

    /**
     * Gets the type of an entity by its name.
     * @param str The name of the entity.
     * @return The type of the entity.
     */
    fun getType(str: String): String {
        val q42 = wbdf.getEntityDocument(getId(str))
        if (q42 is StatementDocument) {
            val pa0 = q42.findStatementGroup("P31")
            if (pa0 != null) {
                val pa = pa0.statements.first().value
                if (pa is ItemIdValue) {
                    return pa.id
                }
            }
        }
        return ""
    }

    /**
     * Gets the label of an entity by its ID.
     * @param str The ID of the entity.
     * @return The label of the entity.
     */
    fun getLabel(str: String): String {
        val q42 = wbdf.getEntityDocument(str)
        return if (q42 is ItemDocument) {
            val text = q42.labels["en"]?.text
            if (text.isNullOrEmpty() || text.startsWith("Wikimedia")) { "" } else { text }
        } else { "" }
    }

    /**
     * Gets a list of classes for an entity by its ID.
     * @param str The ID of the entity.
     * @return A list of classes for the entity.
     */
    fun getClassList(str: String): List<String> {
        val document = wbdf.getEntityDocument(str)
        return if (document is StatementDocument) {
            val group = document.findStatementGroup("P279")
            group?.statements?.mapNotNull { it.value as? ItemIdValue }?.map { it.id } ?: emptyList()
        } else {
            emptyList()
        }
    }

    /**
     * Prints the class definition of an entity by its ID.
     * @param str The ID of the entity.
     */
    fun printClassDefinition(str: String) {
        val label = getLabel(str)
        val label2 = label.replace(" ", "_")
        println("a fjs:$label2 .")
        println("fjs:$label2  rdfs:label \"$label\"@en .")
        println("fjs:$label2  a rdfs:Class .")
        println("fjs:$label2  rdfs:seeAlso <http://www.wikidata.org/entity/$str> .")
        val list = getClassList(str)
        for (i in list) {
            val label4 = getLabel(i)
            val label3 = label4.replace(" ", "_")
            println("fjs:$label2 rdfs:subClassOf fjs:$label3 .")
            println("fjs:$label3 a rdfs:Class .")
            println("fjs:$label3 rdfs:label \"$label4\"@en .")
        }
    }
}
