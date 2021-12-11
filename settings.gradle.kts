pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
    resolutionStrategy {
        eachPlugin {
            if (requested.id.namespace == "org.mini2Dx") {
                useModule("org.mini2Dx:parcl:" + requested.version)
            }
        }
    }
}

rootProject.name = "GitDesk"

include("git")
project(":git").projectDir = file("modules/git")

include("util")
project(":util").projectDir = file("modules/util")

include("data")
project(":data").projectDir = file("modules/data")

include("ui")
project(":ui").projectDir = file("modules/ui")
