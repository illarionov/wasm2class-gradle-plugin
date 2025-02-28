/*
 * SPDX-FileCopyrightText: 2025 Alexey Illarionov and the wasm2class-gradle-plugin project contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package com.example.wasm2class.app.java;

import com.dylibso.chicory.runtime.Instance;
import com.dylibso.chicory.runtime.Store;
import com.dylibso.chicory.wasi.WasiExitException;
import com.dylibso.chicory.wasi.WasiOptions;
import com.dylibso.chicory.wasi.WasiPreview1;

public class Main {
    private static final WasiOptions wasiOptions = WasiOptions.builder().withStdout(System.out).withStderr(System.err).build();

    public static void main(String[] args) {
        try (var wasi = WasiPreview1.builder().withOptions(wasiOptions).build()) {
            runHelloWorld(wasi);
            runClock(wasi);
        }
    }

    private static void runHelloWorld(WasiPreview1 wasi) {
        var store = new Store().addFunction(wasi.toHostFunctions());
        var clockInstance = store.instantiate("helloworld", importValues -> Instance.builder(HelloworldModule.load())
                .withMachineFactory(HelloworldModule::create)
                .withImportValues(importValues)
                .withStart(false)
                .build()
        );
        executeWasiStart(clockInstance);
    }

    private static void runClock(WasiPreview1 wasi) {
        var store = new Store().addFunction(wasi.toHostFunctions());
        var clockInstance = store.instantiate("clock", importValues -> Instance.builder(ClockModule.load())
                .withMachineFactory(ClockModule::create)
                .withImportValues(importValues)
                .withStart(false)
                .build()
        );
        executeWasiStart(clockInstance);
    }

    private static void executeWasiStart(Instance instance) {
        try {
            instance.export("_start").apply();
        } catch (WasiExitException ex) {
            if (ex.exitCode() != 0) {
                throw ex;
            }
        }
    }
}
