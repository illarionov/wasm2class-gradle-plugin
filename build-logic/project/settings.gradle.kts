/*
* SPDX-FileCopyrightText: 2024-2025 Alexey Illarionov and the wasm2class-gradle-plugin project contributors
* SPDX-License-Identifier: Apache-2.0
*/

@file:Suppress("UnstableApiUsage")

pluginManagement {
    repositories {
        google {
            content {
                includeGroupAndSubgroups("android")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            from(files("../../gradle/libs.versions.toml"))
        }
    }
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

include("lint")

rootProject.name = "wasm2class-gradle-project-plugins"
