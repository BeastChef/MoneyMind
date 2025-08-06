// Top-level build file where you can add configuration options common to all sub-projects/modules.

plugins {

    alias(libs.plugins.android.application) apply false

    alias(libs.plugins.kotlin.android) apply false



}
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        // Используем переменную для AGP версии из libs.versions.toml
        classpath("com.android.tools.build:gradle:${libs.versions.agp}")
    }
}