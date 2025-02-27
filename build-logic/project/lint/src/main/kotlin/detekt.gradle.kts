/*
 * SPDX-FileCopyrightText: 2025 Alexey Illarionov and the wasm2class-gradle-plugin project contributors
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Suppress("UnstableApiUsage")

package at.released.wasm2class.buildlogic.project.lint

import io.gitlab.arturbosch.detekt.Detekt

/*
 * Convention plugin that configures a task to run the Detekt static code analyzer
 */
plugins {
    id("io.gitlab.arturbosch.detekt")
}

tasks.register("detektCheck", Detekt::class) {
    description = "Performs Detekt analysis on all modules"

    this.config.setFrom(configRootDir.file("detekt.yml"))
    setSource(
        lintedFileTree.filter { it.name.endsWith(".kt") || it.name.endsWith(".kts") },
    )
    basePath = layout.settingsDirectory.toString()

    parallel = true
    ignoreFailures = false
    buildUponDefaultConfig = true
    allRules = true

    reports {
        html.required = true
        md.required = true
        txt.required = false
        sarif.required = true

        xml.outputLocation = file("build/reports/detekt/report.xml")
        html.outputLocation = file("build/reports/detekt/report.html")
        txt.outputLocation = file("build/reports/detekt/report.txt")
        sarif.outputLocation = file("build/reports/detekt/report.sarif")
    }
}

dependencies {
    detektPlugins(versionCatalogs.named("libs").findLibrary("detekt.formatting").get())
}
