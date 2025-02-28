/*
* SPDX-FileCopyrightText: 2025 Alexey Illarionov and the wasm2class-gradle-plugin project contributors
* SPDX-License-Identifier: Apache-2.0
*/

@file:Suppress("UnstableApiUsage")

pluginManagement {
    includeBuild("build-logic/project")
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

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.9.0"
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

rootProject.name = "wasm2class-gradle-plugin"
include("functional-test-utils")
include("plugin")
