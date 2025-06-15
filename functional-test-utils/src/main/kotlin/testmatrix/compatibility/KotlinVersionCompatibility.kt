/*
 * SPDX-FileCopyrightText: 2025 Alexey Illarionov and the wasm2class-gradle-plugin project contributors
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Suppress("MagicNumber")

package at.released.wasm2class.test.functional.testmatrix.compatibility

import at.released.wasm2class.test.functional.testmatrix.Version
import at.released.wasm2class.test.functional.testmatrix.compatibility.AgpVersionCompatibility.AGP_7_4_0
import at.released.wasm2class.test.functional.testmatrix.compatibility.AgpVersionCompatibility.AGP_8_1_1
import at.released.wasm2class.test.functional.testmatrix.compatibility.AgpVersionCompatibility.AGP_8_5_2
import at.released.wasm2class.test.functional.testmatrix.compatibility.GradleVersionCompatibility.GRADLE_6_8_3
import at.released.wasm2class.test.functional.testmatrix.compatibility.GradleVersionCompatibility.GRADLE_7_6_5
import at.released.wasm2class.test.functional.testmatrix.compatibility.GradleVersionCompatibility.GRADLE_8_1_1
import at.released.wasm2class.test.functional.testmatrix.compatibility.GradleVersionCompatibility.GRADLE_8_5

internal object KotlinVersionCompatibility {
    val KOTLIN_1_9_0 = Version(1, 9, 0)
    val KOTLIN_1_9_20 = Version(1, 9, 20)
    val KOTLIN_2_0_0 = Version(2, 0, 0)
    val KOTLIN_2_0_20 = Version(2, 0, 0)
    val KOTLIN_2_0_21 = Version(2, 0, 21)
    val KOTLIN_2_1_0 = Version(2, 1, 0)
    val KOTLIN_2_1_10 = Version(2, 1, 10)
    val KOTLIN_2_1_21 = Version(2, 1, 21)

    // https://kotlinlang.org/docs/gradle-configure-project.html#apply-the-plugin
    fun isKotlinCompatible(
        kotlin: Version,
        agp: Version,
        gradle: Version,
    ): Boolean = when {
        kotlin >= KOTLIN_2_1_0 -> gradle > GRADLE_7_6_5 && agp > Version(7, 3, 1)
        kotlin >= KOTLIN_2_0_20 -> gradle > GRADLE_6_8_3 && agp in Version(7, 1, 3)..AGP_8_5_2
        kotlin >= KOTLIN_2_0_0 -> gradle in GRADLE_6_8_3..GRADLE_8_5 && agp in Version(7, 1, 3)..Version(8, 3, 1)
        kotlin >= KOTLIN_1_9_20 -> gradle in GRADLE_6_8_3..GRADLE_8_1_1 && agp in Version(4, 2, 2)..AGP_8_1_1
        kotlin >= KOTLIN_1_9_0 -> gradle in GRADLE_6_8_3..GRADLE_7_6_5 && agp in Version(4, 2, 2)..AGP_7_4_0
        else -> false
    }
}
