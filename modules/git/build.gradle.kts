plugins {
    id("org.openjfx.javafxplugin")
}

javafx {
    this.version = "17"
}

dependencies {
    implementation(project(":util"))
}