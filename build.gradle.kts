import org.gradle.api.tasks.testing.TestResult.ResultType.FAILURE
import org.gradle.api.tasks.testing.TestResult.ResultType.SKIPPED
import org.gradle.api.tasks.testing.TestResult.ResultType.SUCCESS
import org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

apply(plugin = "kotlin")
apply(plugin = "kotlin-kapt")

plugins {
    kotlin("jvm") version "1.5.10"
    kotlin("kapt") version "1.6.0"
    application
    jacoco
}

group = "me.peckb"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.google.dagger:dagger:2.40.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0-RC")
    implementation("io.arrow-kt:arrow-core:1.0.1")
    implementation("org.apache.commons:commons-text:1.9")
    implementation("org.apache.commons:commons-math3:3.6.1")
    implementation("org.apache.commons:commons-geometry-core:1.0")
    implementation("org.apache.commons:commons-geometry-euclidean:1.0")

    implementation("com.squareup:kotlinpoet:1.10.2")

    kapt("com.google.dagger:dagger-compiler:2.40.3")
    kaptTest("com.google.dagger:dagger-compiler:2.40.3")

    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.7.0")
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("SKIPPED", "FAILED", "STANDARD_OUT", "STANDARD_ERROR")
        exceptionFormat = FULL
    }
    addTestListener(object : TestListener {
        override fun beforeTest(p0: TestDescriptor?) = Unit
        override fun beforeSuite(p0: TestDescriptor?) = Unit
        override fun afterTest(desc: TestDescriptor, result: TestResult) = Unit
        override fun afterSuite(desc: TestDescriptor, result: TestResult) = printResults(desc, result)
    })
    finalizedBy(tasks.jacocoTestReport)
}
tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        html.required.set(true)
        xml.required.set(true)
    }
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

fun printResults(desc: TestDescriptor, result: TestResult) {
    val ansiReset = "\u001B[0m"

    val ansiYellow = "\u001B[33m"
    val ansiGreen = "\u001B[32m"
    val ansiRed = "\u001B[31m"

    if (desc.displayName.contains("Gradle Test Executor")) {
        val summaryColour = when (result.resultType) {
            SUCCESS -> if (result.skippedTestCount == 0L) ansiGreen else ansiYellow
            SKIPPED -> ansiYellow
            FAILURE, null -> ansiRed
        }

        val output = result.run {
            "Tests: $summaryColour$resultType$ansiReset (" +
              "$ansiGreen$successfulTestCount successes$ansiReset, " +
              "$ansiRed$failedTestCount failures$ansiReset, " +
              "$ansiYellow$skippedTestCount skipped$ansiReset" +
              ")"
        }

        val testResultLine = "| $output |"
        val repeatLength = testResultLine.length -
          (ansiYellow.length + ansiGreen.length + ansiRed.length) -
          (3 * ansiReset.length) -
          (summaryColour.length + ansiReset.length)
        val separationLine = "-".repeat(repeatLength)

        println()
        println(separationLine)
        println(testResultLine)
        println(separationLine)
    }
}

application {
    mainClass.set("me.peckb.aoc.Application")
}

tasks.named<JavaExec>("run") {
    standardInput = System.`in`
}
