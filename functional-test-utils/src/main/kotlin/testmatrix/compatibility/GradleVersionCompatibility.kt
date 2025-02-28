/*
 * SPDX-FileCopyrightText: 2025 Alexey Illarionov and the wasm2class-gradle-plugin project contributors
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Suppress("MagicNumber")

package at.released.wasm2class.test.functional.testmatrix.compatibility

import at.released.wasm2class.test.functional.testmatrix.Version

internal object GradleVersionCompatibility {
    val GRADLE_6_8_3 = Version(6, 8, 3)
    val GRADLE_7_6_3 = Version(7, 6, 3)
    val GRADLE_7_4 = Version(7, 4)
    val GRADLE_8_0 = Version(8, 0)
    val GRADLE_8_1_1 = Version(8, 1, 1)
    val GRADLE_8_2 = Version(8, 2)
    val GRADLE_8_4 = Version(8, 4)
    val GRADLE_8_5 = Version(8, 5)
    val GRADLE_8_6 = Version(8, 6)
    val GRADLE_8_7 = Version(8, 7)
    val GRADLE_8_8 = Version(8, 8)
    val GRADLE_8_9 = Version(8, 9)
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
            8 -> Version(2, 0)
            9 -> Version(4, 3)
            10 -> Version(4, 7)
            11 -> Version(5, 0)
            12 -> Version(5, 4)
            13 -> Version(6, 0)
            14 -> Version(6, 3)
            15 -> Version(6, 7)
            16 -> Version(7, 0)
            17 -> Version(7, 3)
            18 -> Version(7, 5)
            19 -> Version(7, 6)
            20 -> Version(8, 3)
            21 -> Version(8, 5)
            22 -> Version(8, 8)
            23 -> Version(8, 10)
            else -> Version(8, 10)
        }
    } else {
        Version(1, 0)
    }
}
