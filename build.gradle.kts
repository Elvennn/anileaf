plugins {
    base
    kotlin("jvm") version "1.3.50"
}

allprojects {
    group = "io.elven"
    version = "1.0"

    repositories {
        jcenter()
    }
}

dependencies {
    // Make the root project archives configuration depend on every subproject
    subprojects.forEach {
        archives(it)
    }

}