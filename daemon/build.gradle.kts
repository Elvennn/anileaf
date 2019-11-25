plugins {
    java
    kotlin("jvm")
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
    compile(project(":core"))
    compile("org.jetbrains.kotlinx", "kotlinx-coroutines-core", "1.3.2")
}
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> { kotlinOptions.jvmTarget = "11" }


val fatJar = task("fatJar", type = Jar::class) {
    baseName = "${project.name}-fat"
    manifest {
        attributes["Implementation-Title"] = "Gradle Jar File Example"
        attributes["Implementation-Version"] = version
        attributes["Main-Class"] = "io.elven.DaemonKt"
    }
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    with(tasks.jar.get() as CopySpec)
}