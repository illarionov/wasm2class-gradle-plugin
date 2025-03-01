package com.example.wasm2class.android.java.app;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;

import com.dylibso.chicory.log.Logger;
import com.dylibso.chicory.runtime.Instance;
import com.dylibso.chicory.runtime.Store;
import com.dylibso.chicory.wasi.WasiExitException;
import com.dylibso.chicory.wasi.WasiOptions;
import com.dylibso.chicory.wasi.WasiPreview1;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new FrameLayout(this));
        new Thread(MainActivity::testWasm2class).start();
    }

    private static void testWasm2class() {
        WasiOptions wasiOptions = WasiOptions.builder().withStdout(System.out).withStderr(System.err).build();
        Logger logger = new Logger() {
            @Override
            public void log(Level level, String msg, Throwable throwable) {
            }

            @Override
            public boolean isLoggable(Level level) {
                return false;
            }
        };

        try (var wasi = WasiPreview1.builder().withOptions(wasiOptions).withLogger(logger).build()) {
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
