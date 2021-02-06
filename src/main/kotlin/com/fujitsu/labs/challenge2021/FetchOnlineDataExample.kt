package com.fujitsu.labs.challenge2021

import org.wikidata.wdtk.datamodel.interfaces.EntityDocument
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument
import org.wikidata.wdtk.datamodel.interfaces.StatementDocument
import org.wikidata.wdtk.wikibaseapi.WikibaseDataFetcher

fun main(args: Array<String>) {
    ExampleHelpers.configureLogging()
    printDocumentation()
    val wbdf: WikibaseDataFetcher = WikibaseDataFetcher.getWikidataDataFetcher()
//    wbdf.
    println("*** Fetching data for one entity:")
//    val q42: EntityDocument = wbdf.getEntityDocument("Q42")
    val q42 : EntityDocument? = wbdf.getEntityDocumentByTitle("enwiki", "day after dance")
    println(
        "The current revision of the data for entity Q42 is "
                + q42?.getRevisionId()
    )
    println(q42?.entityId?.siteIri)
    if(q42 is StatementDocument) {
        println((q42 as StatementDocument).findStatementGroup("P106"))
/**
        for(i in (q42 as StatementDocument).allStatements) {
            println(i.mainSnak.propertyId.id)
            println(i.subject.iri)
            println(i.value)
            println(i.value.javaClass)
        }
*/
    }
    if (q42 is ItemDocument) {
        println(
            ("The English name for entity Q42 is "
                    + (q42 as ItemDocument).getLabels().get("ja")?.getText())
        )
    }
    println("*** Fetching data for several entities:")
    var results: Map<String, EntityDocument> = wbdf.getEntityDocuments(
        "Q80",
        "P31"
    )
    // Keys of this map are Qids, but we only use the values here:
    for (ed: EntityDocument in results.values) {
        System.out.println(
            ("Successfully retrieved data for "
                    + ed.getEntityId().getId())
        )
    }
    println("*** Fetching data using filters to reduce data volume:")
    // Only site links from English Wikipedia:
    wbdf.getFilter().setSiteLinkFilter(setOf("enwiki"))
    // Only labels in French:
    wbdf.getFilter().setLanguageFilter(setOf("fr"))
    // No statements at all:
    wbdf.getFilter().setPropertyFilter(emptySet())
    val q8: EntityDocument = wbdf.getEntityDocument("Q8")
    if (q8 is ItemDocument) {
        println(
            (("The French label for entity Q8 is "
                    + (q8 as ItemDocument).getLabels().get("fr")?.getText()
                    ) + "\nand its English Wikipedia page has the title "
                    + (q8 as ItemDocument).getSiteLinks().get("enwiki")?.getPageTitle().toString() + ".")
        )
    }
    println("*** Fetching data based on page title:")
    val edPratchett: EntityDocument = wbdf.getEntityDocumentByTitle(
        "enwiki",
        "Terry Pratchett"
    )
    println(
        ("The Qid of Terry Pratchett is "
                + edPratchett.getEntityId().getId())
    )
    println("*** Fetching data based on several page titles:")
    results = wbdf.getEntityDocumentsByTitle(
        "enwiki", "Wikidata",
        "Wikipedia"
    )
    // In this case, keys are titles rather than Qids
    for (entry: Map.Entry<String, EntityDocument> in results.entries) {
        println(
            ("Successfully retrieved data for page entitled \""
                    + entry.key + "\": "
                    + entry.value.getEntityId().getId())
        )
    }
    println("*** Done.")
}

fun printDocumentation() {
    println("********************************************************************")
    println("*** Wikidata Toolkit: FetchOnlineDataExample")
    println("*** ")
    println("*** This program fetches individual data using the wikidata.org API.")
    println("*** It does not download any dump files.")
    println("********************************************************************")
}

class FetchOnlineDataExample {

    /**
     * Prints some basic documentation about this program.
     */
}