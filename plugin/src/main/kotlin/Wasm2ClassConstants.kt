/*
 * SPDX-FileCopyrightText: 2025 Alexey Illarionov and the wasm2class-gradle-plugin project contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package at.released.wasm2class

internal object Wasm2ClassConstants {
    internal const val WASM2CLASS_EXTENSION_NAME = "wasm2class"

    object Deps {
        const val CHICORY_VERSION = "1.1.0"
        const val JAVAPARSER_VERSION = "3.26.3"
        const val CHICORY_GROUP = "com.dylibso.chicory"
        const val CHICORY_AOT = "$CHICORY_GROUP:aot-experimental:$CHICORY_VERSION"
        const val CHICORY_RUNTIME = "$CHICORY_GROUP:runtime:$CHICORY_VERSION"
        const val CHICORY_WASM = "$CHICORY_GROUP:wasm:$CHICORY_VERSION"
        const val JAVAPARSER = "com.github.javaparser:javaparser-symbol-solver-core:$JAVAPARSER_VERSION"
    }

    object Configurations {
        internal const val CHICORY_AOT_COMPILER = "chicoryAotCompiler"
        internal const val CHICORY_AOT_COMPILER_RUNTIME_CLASSPATH = "chicoryAotCompilerRuntimeClasspath"
    }
}
