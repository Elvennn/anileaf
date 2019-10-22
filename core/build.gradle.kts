import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    kotlin("jvm") version "1.3.50"
}

group = "io.elven"
version = "1.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_11
}

repositories {
    jcenter()
    mavenCentral()
}

dependencies {
    compile(kotlin("stdlib-jdk8"))
    testImplementation("junit", "junit", "4.12")
    compile("com.jayway.jsonpath", "json-path", "2.4.0")
    compile("com.fasterxml.jackson.module", "jackson-module-kotlin", "2.9.+")
    compile("org.slf4j", "slf4j-simple", "1.7.28")
    compile("org.simpleframework", "simple-xml", "2.7.1")
    compile("com.dgtlrepublic", "anitomyJ", "0.0.7")
    compile("me.xdrop", "fuzzywuzzy", "1.2.0")
    compile("de.vandermeer", "asciitable", "0.3.2")
}
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> { kotlinOptions.jvmTarget = "11" }

