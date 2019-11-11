plugins {
    base
    kotlin("jvm") version "1.3.50"
}

allprojects {
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

task("deployQnap", type = Exec::class) {
    dependsOn(":daemon:fatJar")
    commandLine("./remoteUpdate.sh")
}