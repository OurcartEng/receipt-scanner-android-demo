pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("s3://public-maven.ourcart.com/release" )
            credentials(AwsCredentials::class) {
                accessKey = ""
                secretKey =""
            }
        }
    }
}

rootProject.name = "receiptScannerAndroidDemo"
include(":app")
 