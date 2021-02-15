package com.fujitsu.labs.challenge2021

import org.wikidata.wdtk.datamodel.interfaces.ItemDocument
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue
import org.wikidata.wdtk.datamodel.interfaces.StatementDocument
import org.wikidata.wdtk.wikibaseapi.WikibaseDataFetcher

fun main(args: Array<String>) {
    val aa = FetchOnlineData()
    val tt = aa.getType("Douglas Adams")
    println("a fjs:$tt .")
    aa.printClassDefinition(tt)
}

class FetchOnlineData {

    private val wbdf: WikibaseDataFetcher = WikibaseDataFetcher.getWikidataDataFetcher()

    fun getId(str: String): String {
        val q43 = wbdf.searchEntities(str, "en", 10)
        return if (q43.isEmpty()) { "" } else { q43.first().entityId }
    }

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

    fun getLabel(str: String): String {
        val q42 = wbdf.getEntityDocument(str)
        return if (q42 is ItemDocument) {
            val text = q42.labels["en"]?.text
            if(text.isNullOrEmpty() || text.startsWith("Wikimedia")) { "" } else {text}
        } else { "" }
    }

    fun getClassList(str: String): List<String> {
        val ret = mutableListOf<String>()
        val q5 = wbdf.getEntityDocument(str)
        if (q5 is StatementDocument) {
            val group = q5.findStatementGroup("P279")
            if (group != null) {
                for (i in group.statements) {
                    val state = i.value
                    if (state is ItemIdValue) {
                        ret.add(state.id)
                    }
                }
            }
        }
        return ret
    }

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
