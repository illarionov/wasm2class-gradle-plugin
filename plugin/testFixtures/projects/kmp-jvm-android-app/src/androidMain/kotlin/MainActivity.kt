/*
 * SPDX-FileCopyrightText: 2025 Alexey Illarionov and the wasm2class-gradle-plugin project contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package com.example.wasm2class.kmp

import android.app.Activity
import android.os.Bundle
import com.dylibso.chicory.runtime.Instance
import com.dylibso.chicory.runtime.Store
import com.dylibso.chicory.wasi.WasiPreview1

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Thread(Companion::testWasm2class).start()
    }

    public companion object {
        private fun testWasm2class() {
            WasiPreview1().use { wasi ->
                runHelloWorld(wasi)
                runClock(wasi)
            }
        }

        private fun runHelloWorld(wasi: WasiPreview1) {
            @Suppress("SpreadOperator")
            val store = Store().addFunction(*wasi.toHostFunctions())
            val clockInstance = store.instantiate("helloworld") { importValues ->
                Instance.builder(Helloworld.load())
                    .withMachineFactory(Helloworld::create)
                    .withImportValues(importValues)
                    .withStart(false)
                    .build()
            }
            clockInstance.executeWasiStart()
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
    }
}
