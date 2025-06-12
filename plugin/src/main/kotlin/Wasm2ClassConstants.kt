/*
 * SPDX-FileCopyrightText: 2025 Alexey Illarionov and the wasm2class-gradle-plugin project contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package at.released.wasm2class

internal object Wasm2ClassConstants {
    internal const val WASM2CLASS_EXTENSION_NAME = "wasm2class"

    object Deps {
        const val CHICORY_VERSION = "1.4.0"
        const val CHICORY_GROUP = "com.dylibso.chicory"
        const val CHICORY_BUILD_TIME_COMPILER = "$CHICORY_GROUP:build-time-compiler:$CHICORY_VERSION"
        const val CHICORY_RUNTIME = "$CHICORY_GROUP:runtime:$CHICORY_VERSION"
    }

    object Configurations {
        internal const val CHICORY_COMPILER = "chicoryCompiler"
        internal const val CHICORY_COMPILER_RUNTIME_CLASSPATH = "chicoryCompilerRuntimeClasspath"
    }
}
