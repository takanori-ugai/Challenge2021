import org.jlleitschuh.gradle.ktlint.reporter.ReporterType

plugins {
    kotlin("jvm") version "1.9.21"
    application
    id("org.jlleitschuh.gradle.ktlint") version "11.0.0"
    id("com.github.johnrengelman.shadow").version("7.1.2")
}

group = "com.fujitsu.labs.challenge2021"
version = "0.2"

val wikidataToolkitVersion = "0.14.1"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test-junit"))
    implementation("org.apache.jena:apache-jena-libs:4.4.0")
    implementation("org.wikidata.wdtk:wdtk-wikibaseapi:$wikidataToolkitVersion")
    implementation("org.wikidata.wdtk:wdtk-dumpfiles:$wikidataToolkitVersion")
    implementation("org.slf4j:slf4j-log4j12:2.+")
}

tasks {
    test {
        useJUnit()
    }

    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }

    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }

    compileJava {
        options.encoding = "UTF-8"
        sourceCompatibility = "1.8"
        targetCompatibility = "1.8"
    }

    compileTestJava {
        options.encoding = "UTF-8"
        sourceCompatibility = "1.8"
        targetCompatibility = "1.8"
    }

    withType<Jar> {
        manifest {
            attributes(mapOf("Main-Class" to "com.fujitsu.labs.challenge2021.SparqlQueryKt"))
        }
    }
}

application {
    mainClass.set("com.fujitsu.labs.challenge2021.SparqlQueryKt")
}

ktlint {
    verbose.set(true)
    outputToConsole.set(true)
    coloredOutput.set(true)
    reporters {
        reporter(ReporterType.CHECKSTYLE)
        reporter(ReporterType.JSON)
        reporter(ReporterType.HTML)
    }
    filter {
        exclude("**/style-violations.kt")
    }
}
