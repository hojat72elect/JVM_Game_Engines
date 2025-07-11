plugins {
    org.openrndr.convention.`kotlin-multiplatform`
    org.openrndr.convention.`publish-multiplatform`
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":openrndr-math"))
                implementation(project(":openrndr-draw"))
                implementation(project(":openrndr-utils"))
                implementation(libs.kotlin.coroutines)
            }
        }
    }
}