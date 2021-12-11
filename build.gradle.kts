fun property(key: String): String? = project.findProperty(key)?.toString()

plugins {
    kotlin("jvm") version "1.6.0" apply false
    id("java")
    application
    id("org.openjfx.javafxplugin") version "0.0.10"
    id("org.mini2Dx.parcl") version "1.8.0"
}

val name = "GitDesk"

allprojects {
    repositories {
        mavenCentral()
        repositories {
            maven("https://harleyoconnor.com/maven/")
        }
    }

    group = "com.harleyoconnor.gitdesk"
    version = "1.0-SNAPSHOT"

    apply(plugin = "java")
    apply(plugin = "org.jetbrains.kotlin.jvm")

    java {
        this.toolchain.languageVersion.set(JavaLanguageVersion.of(17))
        modularity.inferModulePath.set(true)
    }

    dependencies {
        implementation(kotlin("stdlib", "1.6.0"))
        implementation(kotlin("reflect", "1.6.0"))

        implementation(group = "org.apache.logging.log4j", name = "log4j-api", version = "2.15.0")
        implementation(group = "org.apache.logging.log4j", name = "log4j-core", version = "2.15.0")

        testImplementation(group = "org.junit.jupiter", name = "junit-jupiter-api", version = "5.8.2")
        testImplementation(group = "org.junit.jupiter", name = "junit-jupiter-params", version = "5.8.2")
        testRuntimeOnly(group = "org.junit.jupiter", name = "junit-jupiter-engine", version = "5.8.2")
    }

    tasks.test {
        this.useJUnitPlatform()
    }
}

subprojects {
    tasks.compileJava {
        inputs.property("moduleName", project.findProperty("module.name"))
    }
}

javafx {
    this.version = "17"
    this.modules = listOf("javafx.controls", "javafx.fxml")
}

dependencies {
    implementation(project(":util"))
    implementation(project(":data"))
    implementation(project(":ui"))

    implementation(group = "com.harleyoconnor.javautilities", name = "JavaUtilities", version = "0.1.2")
    implementation(group = "com.harleyoconnor.serdes", name = "SerDes", version = "0.0.6")
    implementation(group = "com.squareup.moshi", name = "moshi-kotlin", version = "1.13.0-SNAPSHOT")
}

application {
    this.mainClass.set("$group.MainKt")
    this.applicationDefaultJvmArgs = mutableListOf(
        "--add-modules", "java.scripting" /*,kotlin.reflect" */
    )
}

tasks.getByName<JavaExec>("run") {
    systemProperty("gitdesk.app.syntax_highlighting.force_built_in", "true")
}

val javaHome: String? =
    property("javaHome.17") ?: let {
        project.logger.warn("No Java Home location specified; note that parcl will not include the JRE in distributions.")
        null
    }

parcl {
    exe {
        exeName = name
        withJre(javaHome)
    }
    app {
        appName = name
        icon = "Icon.icns"
        applicationCategory = "public.app-category.developer-tools"
        displayName = name
        identifier = group as String
        copyright = "Copyright 2021 Harley O'Connor"
        withJre(javaHome)
    }
    linux {
        binName = name
        withJre(javaHome)
    }
}