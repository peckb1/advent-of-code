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