/*
 * SPDX-FileCopyrightText: 2025 Alexey Illarionov and the wasm2class-gradle-plugin project contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package at.released.wasm2class.test.functional.testmatrix

import at.released.wasm2class.test.functional.testmatrix.compatibility.AgpVersionCompatibility.AGP_8_8_2
import at.released.wasm2class.test.functional.testmatrix.compatibility.AgpVersionCompatibility.getCompatibleAndroidApiLevel
import at.released.wasm2class.test.functional.testmatrix.compatibility.GradleVersionCompatibility.GRADLE_8_13
import at.released.wasm2class.test.functional.testmatrix.compatibility.KotlinVersionCompatibility.KOTLIN_2_1_10

@Suppress("MagicNumber")
public data class VersionCatalog(
    val gradleVersion: Version,
    val kotlinVersion: Version,
    val chicoryVersion: Version,
    val agpVersion: Version,
    val compileSdk: Int,
    val targetSdk: Int,
    val minSdk: Int = 21,
) {
    val wasm2classPluginVersion: Version = Version(9999, 0, qualifier = "SNAPSHOT")

    public companion object {
        public fun getDefault(): VersionCatalog {
            val agpVersion = AGP_8_8_2
            val compileTargetSdk = getCompatibleAndroidApiLevel(agpVersion)
            val chicoryVersion = System.getenv("TEST_CHICORY_VERSION")?.let(Version::parse) ?: Version(1, 1, 0)
            return VersionCatalog(
                gradleVersion = GRADLE_8_13,
                kotlinVersion = KOTLIN_2_1_10,
                chicoryVersion = chicoryVersion,
                agpVersion = agpVersion,
                compileSdk = compileTargetSdk,
                targetSdk = compileTargetSdk,
            )
        }
    }
}
