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

    implementation(group = "com.harleyoconnor.javautilities", name = "JavaUtilities", version = "0.1.2")
    implementation(group = "com.squareup.moshi", name = "moshi-kotlin", version = "1.13.0-SNAPSHOT")
    implementation(group = "org.fxmisc.richtext", name = "richtextfx", version = "0.10.7")
}