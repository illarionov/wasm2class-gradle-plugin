/*
 * SPDX-FileCopyrightText: 2025 Alexey Illarionov and the wasm2class-gradle-plugin project contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package at.released.wasm2class.test.functional.testproject.fixtures

import at.released.wasm2class.test.functional.testmatrix.VersionCatalog

public fun VersionCatalog.toLibsVersionsToml(): FileContent {
    val versionCatalogText = """
        [versions]
        agp = "$agpVersion"
        kotlin = "$kotlinVersion"
        minSdk = "$minSdk"
        targetSdk = "$targetSdk"
        compileSdk = "$compileSdk"
        wasm2class-gradle-plugin = "$wasm2classPluginVersion"

        [plugins]
        android-application = { id = "com.android.application", version.ref = "agp" }
        android-library = { id = "com.android.application", version.ref = "agp" }
        kotlin-android = { id = "org.jetbrains.kotlin.android", version.ref = "kotlin" }
        kotlin-jvm = { id = "org.jetbrains.kotlin.jvm", version.ref = "kotlin" }
        kotlin-multiplatform = { id = "org.jetbrains.kotlin.multiplatform", version.ref = "kotlin" }
        wasm2class = { id = "at.released.wasm2class.plugin", version.ref = "wasm2class-gradle-plugin" }
        wasm2class-base = { id = "at.released.wasm2class.plugin.base", version.ref = "wasm2class-gradle-plugin" }
    """.trimIndent()

    return FileContent(
        dstPath = "gradle/libs.versions.toml",
        content = versionCatalogText,
    )
}
