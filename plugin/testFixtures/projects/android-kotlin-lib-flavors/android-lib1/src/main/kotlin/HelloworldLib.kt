package com.example.wasm2class.android.kotlin.lib.lib1

import com.dylibso.chicory.runtime.Instance
import com.dylibso.chicory.runtime.Store

public object HelloworldLib {
    fun runHelloWorld() {
        WasiPreview1().use { wasi ->
            val store = Store().addFunction(*wasi.toHostFunctions())
            val clockInstance = store.instantiate("helloworld") { importValues ->
                Instance.builder(HelloworldModule.load())
                    .withMachineFactory(HelloworldModule::create)
                    .withImportValues(importValues)
                    .withStart(false)
                    .build()
            }
            clockInstance.executeWasiStart()
        }
    }
}
