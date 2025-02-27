/*
 * SPDX-FileCopyrightText: 2025 Alexey Illarionov and the wasm2class-gradle-plugin project contributors
 * SPDX-License-Identifier: Apache-2.0
 */

plugins {
    id("at.released.wasm2class.buildlogic.project.lint.detekt")
    id("at.released.wasm2class.buildlogic.project.lint.spotless")
}

tasks.register("styleCheck") {
    group = "Verification"
    description = "Runs code style checking tools (excluding tests)"
    dependsOn("detektCheck", "spotlessCheck")
}
