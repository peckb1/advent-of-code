import org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

apply(plugin = "kotlin")
apply(plugin = "kotlin-kapt")

java {
    sourceCompatibility = JavaVersion.VERSION_1_9
    targetCompatibility = JavaVersion.VERSION_1_9
}

plugins {
    kotlin("jvm") version "1.6.10"
    kotlin("kapt") version "1.6.10"
    application
    jacoco
}

group = "me.peckb"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // main dependencies
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.3")
    implementation("io.arrow-kt:arrow-core:1.1.2")
    implementation("org.apache.commons:commons-text:1.9")
    implementation("org.apache.commons:commons-math3:3.6.1")
    implementation("org.apache.commons:commons-geometry-core:1.0")
    implementation("org.apache.commons:commons-geometry-euclidean:1.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.1")
    // used for generating skeletons
    implementation("com.squareup:kotlinpoet:1.11.0")

    // dependency injection library and annotation procressing
    implementation("com.google.dagger:dagger:2.42")
    kapt("com.google.dagger:dagger-compiler:2.42")
    kaptTest("com.google.dagger:dagger-compiler:2.42")

    // test libraries
    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.2")
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("SKIPPED", "FAILED", "STANDARD_OUT", "STANDARD_ERROR")
        exceptionFormat = FULL
    }
    finalizedBy(tasks.jacocoTestReport)
    maxParallelForks = (Runtime.getRuntime().availableProcessors() / 2 + 1)
}
tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        html.required.set(true)
        xml.required.set(true)
    }
    classDirectories.setFrom(files(classDirectories.files.map {
        fileTree(it) {
            exclude("**/skeleton/**", "**/Application*")
        }
    }))
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}

jacoco {
    toolVersion = "0.8.7"
}

sourceSets {
    test {
        java {
            setSrcDirs(listOf("build/generated/source/kapt/test"))
        }
    }
}

application {
    mainClass.set("me.peckb.aoc.Application")
}

tasks.named<JavaExec>("run") {
    standardInput = System.`in`
}
