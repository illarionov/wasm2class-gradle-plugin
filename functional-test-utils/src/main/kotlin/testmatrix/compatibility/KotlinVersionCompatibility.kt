/*
 * SPDX-FileCopyrightText: 2025 Alexey Illarionov and the wasm2class-gradle-plugin project contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package at.released.wasm2class.test.functional.testmatrix.compatibility

import at.released.wasm2class.test.functional.testmatrix.Version

internal object KotlinVersionCompatibility {
    val KOTLIN_2_0_20 = Version(2, 0, 20)
    val KOTLIN_2_1_10 = Version(2, 1, 10)
    val KOTLIN_2_1_20_RC = Version(2, 1, 20, "RC")
}
