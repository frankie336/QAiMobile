// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
    alias(libs.plugins.googleDevtoolsKsp) apply false
    alias(libs.plugins.hiltAndroid) apply false
    alias(libs.plugins.kotlin.parcelize) apply false
}

buildscript {
    val kotlin_version = "1.8.0"
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version")
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.46")
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}