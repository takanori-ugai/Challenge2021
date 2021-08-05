import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.20"
    application
    id("com.github.johnrengelman.shadow").version("6.1.0")
}

group = "com.fujitsu.labs.challenge2021"
version = "0.1"

val ktlintCfg by configurations.creating
val wikidataToolkitVersion = "0.11.1"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test-junit"))
    implementation("org.apache.jena:apache-jena-libs:3.17.0")
    implementation("org.wikidata.wdtk:wdtk-wikibaseapi:$wikidataToolkitVersion")
    implementation("org.wikidata.wdtk:wdtk-dumpfiles:$wikidataToolkitVersion")
    implementation("org.slf4j:slf4j-log4j12:1.7.10")
    ktlintCfg("com.pinterest:ktlint:0.40.0")
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
    mainClass.set("com.fujitsu.labs.challenge2021.SparqlQueryKt")
}

val ktlintFormat by tasks.creating(JavaExec::class) {
  group = "formatting"
  main = "com.pinterest.ktlint.Main"
  classpath = ktlintCfg
  args("-F", "src/**/*.kt")

}

