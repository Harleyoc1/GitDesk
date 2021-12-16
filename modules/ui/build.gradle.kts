plugins {
    id("org.openjfx.javafxplugin")
}

javafx {
    this.version = "17"
    this.modules = listOf("javafx.controls", "javafx.fxml")
}

dependencies {
    implementation(project(":util"))
    implementation(project(":git"))
    implementation(project(":data"))

    implementation(group = "org.fxmisc.richtext", name = "richtextfx", version = "0.10.7")
}