plugins {
    id("org.openjfx.javafxplugin")
}

javafx {
    this.version = "17"
    this.modules = listOf("javafx.controls", "javafx.fxml")
}

dependencies {
    implementation(project(":util"))
    implementation(project(":data"))
}