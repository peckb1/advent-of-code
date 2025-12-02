import org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

kotlin {
    jvmToolchain(21)
}

plugins {
    kotlin("jvm") version "2.2.0"
    id("com.google.devtools.ksp") version "2.2.20-2.0.4"
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
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.20.1")
    implementation("io.arrow-kt:arrow-core:2.2.0")
    implementation("io.arrow-kt:arrow-functions:2.2.0")
    implementation("org.apache.commons:commons-text:1.14.0")
    implementation("org.apache.commons:commons-math3:3.6.1")
    implementation("org.apache.commons:commons-geometry-core:1.0")
    implementation("org.apache.commons:commons-geometry-euclidean:1.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
    implementation("tools.aqua:z3-turnkey:4.14.1")
    implementation("org.jgrapht:jgrapht-core:1.5.2")

    // used for generating skeletons
    implementation("com.squareup:kotlinpoet:2.2.0")

    // dependency injection library and annotation processing
    implementation("com.google.dagger:dagger:2.57.2")
    ksp("com.google.dagger:dagger-compiler:2.57.2")
    kspTest("com.google.dagger:dagger-compiler:2.57.2")

    // test libraries
    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter:6.0.1")
    testImplementation("org.junit.platform:junit-platform-launcher:6.0.1")
}

tasks.test {
    filter {
        val year: String? = project.findProperty("year") as? String
        if (year != null) {
            includeTestsMatching("me.peckb.aoc._${year}.*")
        }
    }
    
    useJUnitPlatform()
    testLogging {
        events("SKIPPED", "FAILED", "STANDARD_OUT", "STANDARD_ERROR")
        exceptionFormat = FULL
        showStandardStreams = true
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
            val year: String? = project.findProperty("year") as? String
            if (year != null) {
              include("**/_${year}/**")
            }
        }
    }))
}

jacoco {
    toolVersion = "0.8.14"
}

sourceSets {
    test {
        java {
            setSrcDirs(listOf("build/generated/ksp/test/kotlin"))
        }
    }
}

application {
    mainClass.set("me.peckb.aoc.Application")
}

tasks.named<JavaExec>("run") {
    standardInput = System.`in`
}
