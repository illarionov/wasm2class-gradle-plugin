/*
 * SPDX-FileCopyrightText: 2025 Alexey Illarionov and the wasm2class-gradle-plugin project contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package com.example.wasm2class.android.kotlin.lib.lib1

import com.dylibso.chicory.runtime.ImportValues
import com.dylibso.chicory.runtime.Instance
import com.dylibso.chicory.runtime.Store
import com.example.wasm2class.android.kotlin.lib.lib1.clock.ClockModule

public object StagingClockLib {
    fun runClock() {
        WasiPreview1().use { wasi ->
            @Suppress("SpreadOperator")
            val store = Store().addFunction(*wasi.toHostFunctions())
            val clockInstance = store.instantiate("clock") { importValues: ImportValues? ->
                Instance.builder(ClockModule.load())
                    .withMachineFactory(ClockModule::create)
                    .withImportValues(importValues)
                    .withStart(false)
                    .build()
            }
            clockInstance.executeWasiStart()
        }
    }
}
