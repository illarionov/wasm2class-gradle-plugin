/*
 * SPDX-FileCopyrightText: 2025 Alexey Illarionov and the wasm2class-gradle-plugin project contributors
 * SPDX-License-Identifier: Apache-2.0
 */

import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.jvm)
}

group = "at.released.wasm2class"

dependencies {
    api(gradleApi())
    api(gradleTestKit())
    api(libs.assertk)
    api(libs.junit.jupiter.api)
    api(platform(libs.junit.bom))

    testImplementation(libs.assertk)
    testImplementation(libs.junit.jupiter.api)
    testImplementation(libs.junit.jupiter.params)
    testRuntimeOnly(libs.junit.jupiter.engine)
}

kotlin {
    explicitApi = ExplicitApiMode.Warning
    compilerOptions {
        jvmTarget = JvmTarget.JVM_11
        // https://docs.gradle.org/current/userguide/compatibility.html#kotlin
        freeCompilerArgs.addAll(
            "-opt-in=kotlin.RequiresOptIn",
            "-Xjvm-default=all",
        )
    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

tasks.withType<JavaCompile>().configureEach {
    sourceCompatibility = JavaVersion.VERSION_11.toString()
    targetCompatibility = JavaVersion.VERSION_11.toString()
}
