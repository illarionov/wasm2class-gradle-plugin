/*
 * SPDX-FileCopyrightText: 2025 Alexey Illarionov and the wasm2class-gradle-plugin project contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package com.example.wasm2class.kotlin.app

import com.dylibso.chicory.runtime.Instance
import com.dylibso.chicory.runtime.Store
import com.dylibso.chicory.wasi.WasiExitException
import com.dylibso.chicory.wasi.WasiOptions
import com.dylibso.chicory.wasi.WasiPreview1

fun main() {
    testWasm2class()
}

private fun testWasm2class() {
    val wasiOptions = WasiOptions.builder().withStdout(System.out).withStderr(System.err).build()
    WasiPreview1.builder().withOptions(wasiOptions).build().use { wasi ->
        runHelloWorld(wasi)
        runClock(wasi)
    }
}

private fun runHelloWorld(wasi: WasiPreview1) {
    @Suppress("SpreadOperator")
    val store = Store().addFunction(*wasi.toHostFunctions())
    val instance = store.instantiate("helloworld") { importValues ->
        Instance.builder(Helloworld.load())
            .withMachineFactory(Helloworld::create)
            .withImportValues(importValues)
            .withStart(false)
            .build()
    }
    instance.executeWasiStart()
}

private fun runClock(wasi: WasiPreview1) {
    @Suppress("SpreadOperator")
    val store = Store().addFunction(*wasi.toHostFunctions())
    val clockInstance = store.instantiate("clock") { importValues ->
        Instance.builder(Clock.load())
            .withMachineFactory(Clock::create)
            .withImportValues(importValues)
            .withStart(false)
            .build()
    }
    clockInstance.executeWasiStart()
}

private fun Instance.executeWasiStart() {
    try {
        export("_start").apply()
    } catch (ex: WasiExitException) {
        if (ex.exitCode() != 0) {
            throw ex
        }
    }
}
