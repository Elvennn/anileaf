import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    kotlin("jvm") version "1.3.41"
}

group = "io.elven"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    testCompile("junit", "junit", "4.12")
    compile("com.jayway.jsonpath", "json-path" ,"2.4.0")
    runtime("com.fasterxml.jackson.core:jackson-databind:2.4.5")
    compile("org.slf4j", "slf4j-simple", "1.7.28")
    compile("org.simpleframework", "simple-xml", "2.7.1")
    compile("com.dgtlrepublic", "anitomyJ", "0.0.7")
}