/*
 * SPDX-FileCopyrightText: 2025 Alexey Illarionov and the wasm2class-gradle-plugin project contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package at.released.wasm2class

import java.util.Locale

internal fun String.capitalizeAscii(): String = replaceFirstChar {
    if (it.isLowerCase()) {
        it.titlecase(Locale.ROOT)
    } else {
        it.toString()
    }
}

internal fun String.toUpperCamelCase(): String = this
    .split("-", "_")
    .filter(String::isNotEmpty)
    .joinToString("", transform = String::capitalizeAscii)
