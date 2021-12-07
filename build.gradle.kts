import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

apply(plugin = "kotlin")
apply(plugin = "kotlin-kapt")

plugins {
    kotlin("jvm") version "1.5.10"
    kotlin("kapt") version "1.6.0"
}

group = "me.peckb"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.google.dagger:dagger:2.40.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0-RC")

    kapt("com.google.dagger:dagger-compiler:2.40.3")
    kaptTest("com.google.dagger:dagger-compiler:2.40.3")

    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.7.0")
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("PASSED", "SKIPPED", "FAILED", "STANDARD_OUT", "STANDARD_ERROR")
    }
    addTestListener(object : TestListener {
        override fun beforeTest(p0: TestDescriptor?) = Unit
        override fun beforeSuite(p0: TestDescriptor?) = Unit
        override fun afterTest(desc: TestDescriptor, result: TestResult) = Unit
        override fun afterSuite(desc: TestDescriptor, result: TestResult) = printResults(desc, result)
    })
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "11"
}

sourceSets {
    test {
        java {
            setSrcDirs(listOf("build/generated/source/kapt/test"))
        }
    }
}

fun printResults(desc: TestDescriptor, result: TestResult) {
    if (desc.displayName.contains("Gradle Test Executor")) {
        val output = result.run {
            "Test Results: $resultType (" +
              "$testCount tests, " +
              "$successfulTestCount successes, " +
              "$failedTestCount failures, " +
              "$skippedTestCount skipped" +
              ")"
        }

        val testResultLine = "|  $output  |"
        val repeatLength = testResultLine.length
        val separationLine = "-".repeat(repeatLength)

        println()
        println(separationLine)
        println(testResultLine)
        println(separationLine)
    }
}