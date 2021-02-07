package com.fujitsu.labs.challenge2021

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.rdf.model.RDFNode;

fun main(args: Array<String>) {
    val from = if(args.size==0) { "http://kgc.knowledge-graph.jp/data/SpeckledBand" } else { args[0] }
    val queryString = """
        PREFIX kgc: <http://kgc.knowledge-graph.jp/ontology/kgc.owl#>
        prefix rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
        prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>
        SELECT DISTINCT *
        FROM <$from>
        #FROM <http://kgc.knowledge-graph.jp/data/SpeckledBand>
        #FROM <http://kgc.knowledge-graph.jp/data/DancingMen>
        #FROM <http://kgc.knowledge-graph.jp/data/ACaseOfIdentity>
        #FROM <http://kgc.knowledge-graph.jp/data/DevilsFoot>
        #FROM <http://kgc.knowledge-graph.jp/data/CrookedMan>
        #FROM <http://kgc.knowledge-graph.jp/data/AbbeyGrange>
        #FROM <http://kgc.knowledge-graph.jp/data/SilverBlaze>
        #FROM <http://kgc.knowledge-graph.jp/data/ResidentPatient>
        WHERE {
          ?s rdfs:label ?o .
          {
          ?s rdf:type    kgc:Object .
          } UNION {
          ?s rdf:type    kgc:OFobj .
          } UNION {
          ?s rdf:type    kgc:PhysicalObject .
          }
        }
    """
    val query: Query = QueryFactory.create(queryString)
    val qexec: QueryExecution = QueryExecutionFactory.sparqlService("http://kg.hozo.jp/fuseki/kgrc2020v2/sparql", query)
    val results: ResultSet = qexec.execSelect()

    printHeader()
//    ResultSetFormatter.out(System.out, results, query)
    val wikiFetch = FetchOnlineData()
    for(i in results) {
        val str = i.getLiteral("o").string
//        println(str)
        val tt = wikiFetch.getType(str)
        if(tt == "") {
            val list = wikiFetch.getClassList(wikiFetch.getId(str))
            for(i2 in list) {
                val label4 = wikiFetch.getLabel(i2)
                val label3 = label4?.replace(" ", "_")
                println("<${i.getResource("s").uri}>  rdfs:subClassOf fjs:$label3 .")
                println("fjs:$label3 a rdfs:Class .")
                println("fjs:$label3 rdfs:label \"$label4\"@en .")
            }

        } else {
        //    println(tt)
            print("<${i.getResource("s").uri}> ")
            wikiFetch.printClassDefinition(tt)
        }
    }

}

fun printHeader() {
    println("@prefix rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .\n" +
            "@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> .\n" +
            "@prefix fjs: <http://challenge2021.labs.fujitsu.com/ontology/kgc.owl#> . \n")
}

class SparqlQuery {
}