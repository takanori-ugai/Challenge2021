import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.30"
    application
    id("com.github.johnrengelman.shadow").version("6.1.0")
}

group = "com.fujitsu.labs.challenge2021"
version = "0.1"

val wikidataToolkitVersion = "0.11.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test-junit"))
    implementation("org.apache.jena:apache-jena-libs:3.17.0")
    implementation("org.wikidata.wdtk:wdtk-wikibaseapi:0.11.0")
    implementation("org.wikidata.wdtk:wdtk-dumpfiles:0.11.0")
    implementation("org.slf4j:slf4j-log4j12:1.7.10")
}

tasks.test {
    useJUnit()
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}

tasks {
    withType<Jar> {
        manifest {
            attributes(mapOf("Main-Class" to "com.fujitsu.labs.challenge2021.SparqlQueryKt"))
        }
    }

}

application {
    mainClassName = "com.fujitsu.labs.challenge2021.SparqlQueryKt"
}