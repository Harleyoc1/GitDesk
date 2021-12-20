plugins {
    id("org.openjfx.javafxplugin")
}

javafx {
    this.version = "17"
}

dependencies {
    implementation(project(":util"))
    implementation(project(":git"))

    implementation(group = "com.harleyoconnor.javautilities", name = "JavaUtilities", version = "0.1.2")
    implementation(group = "com.squareup.moshi", name = "moshi-kotlin", version = "1.13.0-SNAPSHOT")
}

tasks.test {
    systemProperty("gitdesk.session_key", project.findProperty("gitdesk.session_key").toString())
}
