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
    implementation(group = "com.google.guava", name = "guava", version = "31.0.1-jre")
}

tasks.test {
    val sessionKey = project.findProperty("gitdesk.session_key")
    if (sessionKey != null) {
        systemProperty("gitdesk.session_key", sessionKey.toString())
    }
}
