/*
 * SPDX-FileCopyrightText: 2025 Alexey Illarionov and the wasm2class-gradle-plugin project contributors
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Suppress("MagicNumber")

package at.released.wasm2class.test.functional.testmatrix.compatibility

import at.released.wasm2class.test.functional.testmatrix.Version
import at.released.wasm2class.test.functional.testmatrix.compatibility.GradleVersionCompatibility.GRADLE_7_4
import at.released.wasm2class.test.functional.testmatrix.compatibility.GradleVersionCompatibility.GRADLE_8_0
import at.released.wasm2class.test.functional.testmatrix.compatibility.GradleVersionCompatibility.GRADLE_8_10_2
import at.released.wasm2class.test.functional.testmatrix.compatibility.GradleVersionCompatibility.GRADLE_8_2
import at.released.wasm2class.test.functional.testmatrix.compatibility.GradleVersionCompatibility.GRADLE_8_4
import at.released.wasm2class.test.functional.testmatrix.compatibility.GradleVersionCompatibility.GRADLE_8_6
import at.released.wasm2class.test.functional.testmatrix.compatibility.GradleVersionCompatibility.GRADLE_8_7
import at.released.wasm2class.test.functional.testmatrix.compatibility.GradleVersionCompatibility.GRADLE_8_9

internal object AgpVersionCompatibility {
    val AGP_7_0_0 = Version(7, 0, 0)
    val AGP_7_2_0 = Version(7, 2, 0)
    val AGP_7_3_0 = Version(7, 3, 0)
    val AGP_7_4_0 = Version(7, 4, 0)
    val AGP_8_0_0 = Version(8, 0, 0)
    val AGP_8_0_2 = Version(8, 0, 2)
    val AGP_8_1_1 = Version(8, 1, 1)
    val AGP_8_2_0 = Version(8, 2, 0)
    val AGP_8_2_2 = Version(8, 2, 0)
    val AGP_8_3_0 = Version(8, 3, 0)
    val AGP_8_4_0 = Version(8, 4, 0)
    val AGP_8_5_0 = Version(8, 5, 0)
    val AGP_8_5_2 = Version(8, 5, 2)
    val AGP_8_6_0 = Version(8, 6, 0)
    val AGP_8_7_0 = Version(8, 7, 0)
    val AGP_8_8_0 = Version(8, 8, 0)
    val AGP_8_8_2 = Version(8, 8, 2)

    // Checks if a AGP version [agpVersion] can run on the current JVM
    fun isAgpCompatibleWithRuntime(agpVersion: Version): Boolean {
        val jvmVersion = Runtime.version().version()[0]
        return when {
            agpVersion >= AGP_8_3_0 -> jvmVersion >= 17
            agpVersion >= AGP_8_0_0 -> jvmVersion in 17..20
            agpVersion >= AGP_7_0_0 -> jvmVersion >= 11
            else -> jvmVersion in 8..11
        }
    }

    // Checks if a AGP version [agpVersion] is compatible with a [gradleVersion] version of Gradle
    // See https://developer.android.com/build/releases/past-releases
    fun agpIsCompatibleWithGradle(
        agpVersion: Version,
        gradleVersion: Version,
    ) = when {
        agpVersion >= AGP_8_8_0 -> gradleVersion >= GRADLE_8_10_2
        agpVersion >= AGP_8_7_0 -> gradleVersion >= GRADLE_8_9
        agpVersion >= AGP_8_5_0 -> gradleVersion >= GRADLE_8_7
        agpVersion >= AGP_8_4_0 -> gradleVersion >= GRADLE_8_6
        agpVersion >= AGP_8_3_0 -> gradleVersion >= GRADLE_8_4
        agpVersion >= AGP_8_2_0 -> gradleVersion >= GRADLE_8_2
        agpVersion >= AGP_8_0_0 -> gradleVersion >= GRADLE_8_0
        agpVersion >= AGP_7_4_0 -> gradleVersion >= GRADLE_7_4
        agpVersion >= AGP_7_3_0 -> gradleVersion >= GRADLE_7_4 && gradleVersion < GRADLE_8_0
        else -> false
    }

    // https://developer.android.com/build/releases/gradle-plugin#api-level-support
    fun getCompatibleAndroidApiLevel(
        agpVersion: Version,
    ): Int = when {
        agpVersion >= AGP_8_6_0 -> 35
        agpVersion >= AGP_8_1_1 -> 34
        agpVersion >= AGP_7_2_0 -> 33
        else -> 32
    }
}
