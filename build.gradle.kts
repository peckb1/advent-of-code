import org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL

apply(plugin = "kotlin")
apply(plugin = "kotlin-kapt")

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    jvmToolchain(17)
}

plugins {
    kotlin("jvm") version "1.9.22"
    kotlin("kapt") version "1.9.22"
    application
    jacoco
}

group = "me.peckb"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    // main dependencies
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.17.1")
    implementation("io.arrow-kt:arrow-core:1.2.4")
    implementation("org.apache.commons:commons-text:1.11.0")
    implementation("org.apache.commons:commons-math3:3.6")
    implementation("org.apache.commons:commons-geometry-core:1.0")
    implementation("org.apache.commons:commons-geometry-euclidean:1.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
    implementation("tools.aqua:z3-turnkey:4.13.0")
    implementation("org.jgrapht:jgrapht-core:1.5.2")

    // used for generating skeletons
    implementation("com.squareup:kotlinpoet:1.18.1")

    // dependency injection library and annotation procressing
    implementation("com.google.dagger:dagger:2.51")
    kapt("com.google.dagger:dagger-compiler:2.51")
    kaptTest("com.google.dagger:dagger-compiler:2.51")

    // test libraries
    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.3")
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("SKIPPED", "FAILED", "STANDARD_OUT", "STANDARD_ERROR")
        exceptionFormat = FULL
    }
    finalizedBy(tasks.jacocoTestReport)
    maxParallelForks = ((Runtime.getRuntime().availableProcessors() / 3.0) * 2.0).toInt()
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

jacoco {
    toolVersion = "0.8.11"
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
