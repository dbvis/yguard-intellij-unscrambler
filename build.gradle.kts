plugins {
    id("java")
    id("org.jetbrains.intellij") version "1.13.3"
}

group = "com.dbvis.yguard-unscrambler"
version = "1.1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.yworks:yguard:4.0.0")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

intellij {
    version.set("2023.1")
    plugins.set(listOf("java"))
}

tasks {
    buildSearchableOptions {
        enabled = false
    }

    patchPluginXml {
        version.set("${project.version}")
        sinceBuild.set("222")
    }
}
