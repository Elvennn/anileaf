import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    kotlin("jvm") version "1.3.41"
}

group = "io.elven"
version = "1.0-SNAPSHOT"

repositories {
    jcenter()
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    testImplementation("junit", "junit", "4.12")
    implementation("com.jayway.jsonpath", "json-path" ,"2.4.0")
    implementation("com.fasterxml.jackson.module", "jackson-module-kotlin", "2.9.+")
    implementation("org.slf4j", "slf4j-simple", "1.7.28")
    implementation("org.simpleframework", "simple-xml", "2.7.1")
    implementation("com.dgtlrepublic", "anitomyJ", "0.0.7")
    implementation("me.xdrop", "fuzzywuzzy","1.2.0")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> { kotlinOptions.jvmTarget = "1.8" }