import org.jlleitschuh.gradle.ktlint.reporter.ReporterType

plugins {
    kotlin("jvm") version "2.0.0"
    application
    id("org.jlleitschuh.gradle.ktlint") version "12.1.1"
    id(
        "com.github.johnrengelman.shadow",
    ).version(
        "7.1.2",
    )
}

group = "com.fujitsu.labs.challenge2021"
version = "0.2"

val wikidataToolkitVersion =
    "0.14.7"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(
        kotlin(
            "test-junit",
        ),
    )
    implementation(
        "org.apache.jena:apache-jena-libs:5.1.0",
    )
    implementation(
        "org.wikidata.wdtk:wdtk-wikibaseapi:$wikidataToolkitVersion",
    )
    implementation(
        "org.wikidata.wdtk:wdtk-dumpfiles:$wikidataToolkitVersion",
    )
    implementation(
        "org.slf4j:slf4j-log4j12:2.+",
    )
}

tasks {
    test {
        useJUnit()
    }

    compileKotlin {
        kotlinOptions.jvmTarget = "11"
    }

    compileTestKotlin {
        kotlinOptions.jvmTarget = "11"
    }

    compileJava {
        options.encoding = "UTF-8"
        sourceCompatibility = "11"
        targetCompatibility = "11"
    }

    compileTestJava {
        options.encoding = "UTF-8"
        sourceCompatibility = "11"
        targetCompatibility = "11"
    }

    withType<Jar> {
        manifest {
            attributes(
                mapOf(
                    "Main-Class" to "com.fujitsu.labs.challenge2021.SparqlQueryKt",
                ),
            )
        }
    }
}

application {
    mainClass.set(
        "com.fujitsu.labs.challenge2021.SparqlQueryKt",
    )
}

ktlint {
    verbose.set(
        true,
    )
    outputToConsole.set(
        true,
    )
    coloredOutput.set(
        true,
    )
    additionalEditorconfig.set(
        mapOf(
            "max_line_length" to "180",
        ),
    )
    reporters {
        reporter(
            ReporterType.CHECKSTYLE,
        )
        reporter(
            ReporterType.JSON,
        )
        reporter(
            ReporterType.HTML,
        )
    }
    filter {
        exclude(
            "**/style-violations.kt",
        )
    }
}
