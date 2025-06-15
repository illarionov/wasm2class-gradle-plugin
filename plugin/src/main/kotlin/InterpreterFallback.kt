/*
 * SPDX-FileCopyrightText: 2025 Alexey Illarionov and the wasm2class-gradle-plugin project contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package at.released.wasm2class

/**
 * Enum representing the fallback behavior for when the compiler needs to fallback to using
 * the interpreter.
 */
public enum class InterpreterFallback {
    /**
     * The compiler will silently use the interpreter as a fallback without any notification.
     */
    SILENT,

    /**
     * The compiler will log a warning message to stderr when it falls back to using the interpreter.
     */
    WARN,

    /**
     * The compiler will throw an exception if it needs to fall back to using the interpreter.
     */
    FAIL,
}
