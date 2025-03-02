package com.example.wasm2class.android.kotlin.lib.lib1

import com.dylibso.chicory.runtime.ImportValues
import com.dylibso.chicory.runtime.Instance
import com.dylibso.chicory.runtime.Store

public object StagingClockLib {
    fun runClock() {
        WasiPreview1().use { wasi ->
            val store = Store().addFunction(*wasi.toHostFunctions())
            val clockInstance = store.instantiate(
                "clock"
            ) { importValues: ImportValues? ->
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
