/*
 * SPDX-FileCopyrightText: 2025 Alexey Illarionov and the wasm2class-gradle-plugin project contributors
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Suppress("MagicNumber")

package at.released.wasm2class.test.functional.testmatrix.compatibility

import at.released.wasm2class.test.functional.testmatrix.Version

internal object GradleVersionCompatibility {
    val GRADLE_2_0 = Version(2, 0)
    val GRADLE_4_3 = Version(4, 3)
    val GRADLE_4_7 = Version(4, 7)
    val GRADLE_5_0 = Version(5, 0)
    val GRADLE_5_4 = Version(5, 4)
    val GRADLE_6_0 = Version(6, 0)
    val GRADLE_6_3 = Version(6, 3)
    val GRADLE_6_7 = Version(6, 7)
    val GRADLE_7_0 = Version(7, 0)
    val GRADLE_7_3 = Version(7, 3)
    val GRADLE_7_5 = Version(7, 5)
    val GRADLE_7_6 = Version(7, 6)
    val GRADLE_7_6_4 = Version(7, 6, 4)
    val GRADLE_8_3 = Version(8, 3)
    val GRADLE_8_5 = Version(8, 5)
    val GRADLE_8_6 = Version(8, 6)
    val GRADLE_8_7 = Version(8, 7)
    val GRADLE_8_8 = Version(8, 8)
    val GRADLE_8_9 = Version(8, 9)
    val GRADLE_8_10 = Version(8, 10)
    val GRADLE_8_10_2 = Version(8, 10, 2)
    val GRADLE_8_13 = Version(8, 13)

    // Checks if a Gradle version can run on the current JVM
    fun isGradleCompatibleWithRuntime(gradleVersion: Version): Boolean {
        val jvmVersion = Runtime.version().version()[0]
        return gradleVersion >= getMinimumGradleVersionOnJvm(jvmVersion)
    }

    // https://docs.gradle.org/current/userguide/compatibility.html#java
    @Suppress("CyclomaticComplexMethod")
    private fun getMinimumGradleVersionOnJvm(jvmVersion: Int): Version = if (jvmVersion >= 8) {
        when (jvmVersion) {
            8 -> GRADLE_2_0
            9 -> GRADLE_4_3
            10 -> GRADLE_4_7
            11 -> GRADLE_5_0
            12 -> GRADLE_5_4
            13 -> GRADLE_6_0
            14 -> GRADLE_6_3
            15 -> GRADLE_6_7
            16 -> GRADLE_7_0
            17 -> GRADLE_7_3
            18 -> GRADLE_7_5
            19 -> GRADLE_7_6
            20 -> GRADLE_8_3
            21 -> GRADLE_8_5
            22 -> GRADLE_8_8
            23 -> GRADLE_8_10
            else -> GRADLE_8_10
        }
    } else {
        Version(1, 0)
    }
}
