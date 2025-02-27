/*
 * SPDX-FileCopyrightText: 2025 Alexey Illarionov and the wasm2class-gradle-plugin project contributors
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Suppress("UnstableApiUsage")

package at.released.wasm2class.buildlogic.project.lint

import org.gradle.api.Project
import org.gradle.api.file.Directory
import org.gradle.api.file.FileTree
import org.gradle.api.tasks.util.PatternFilterable

internal val Project.configRootDir: Directory get() = layout.settingsDirectory.dir("config")

internal val Project.lintedFileTree: FileTree
    get() = layout.settingsDirectory.asFileTree.matching {
        excludeNonLintedDirectories()
    }

internal fun PatternFilterable.excludeNonLintedDirectories() {
    exclude {
        it.isDirectory && it.name in excludedDirectories
    }
    exclude {
        it.isDirectory && it.relativePath.startsWith("config/copyright")
    }
    exclude("**/api/**/*.api")
}

private val excludedDirectories = setOf(
    ".git",
    ".gradle",
    ".idea",
    "build",
    "generated",
    "out",
)
