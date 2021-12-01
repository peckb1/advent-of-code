import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

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
    implementation("org.junit.jupiter:junit-jupiter:5.7.0")

    kapt("com.google.dagger:dagger-compiler:2.40.3")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "11"
}
