/*
 * SPDX-FileCopyrightText: 2025 Alexey Illarionov and the wasm2class-gradle-plugin project contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package com.example.wasm2class.kmp

import com.dylibso.chicory.log.Logger
import com.dylibso.chicory.runtime.Instance
import com.dylibso.chicory.wasi.WasiExitException
import com.dylibso.chicory.wasi.WasiOptions
import com.dylibso.chicory.wasi.WasiPreview1

internal fun WasiPreview1(
    block: WasiPreview1.Builder.() -> Unit = {},
): WasiPreview1 = WasiPreview1.builder().apply {
    withOptions(stdioWasiOptions)
    withLogger(DummyWasiLogger)
    block(this)
}.build()

internal val stdioWasiOptions = WasiOptions.builder().withStdout(System.out)
    .withStderr(System.err)
    .build()

internal fun Instance.executeWasiStart() {
    try {
        export("_start").apply()
    } catch (ex: WasiExitException) {
        if (ex.exitCode() != 0) {
            throw ex
        }
    }
}

internal object DummyWasiLogger : Logger {
    override fun log(level: Logger.Level, msg: String, throwable: Throwable) = Unit
    override fun isLoggable(level: Logger.Level): Boolean = false
}
