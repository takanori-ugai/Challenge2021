plugins {
    kotlin("multiplatform") version "1.4.30"
}

group = "com.fujitsu.labs.challenge2021"
version = "0.1"

repositories {
    mavenCentral()
}

kotlin {
    
    sourceSets {
        val commonMain by getting
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
    }
}
