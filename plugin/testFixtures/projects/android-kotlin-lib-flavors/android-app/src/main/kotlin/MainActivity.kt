package com.example.wasm2class.android.kotlin.lib.app

import android.app.Activity
import android.os.Bundle
import android.widget.FrameLayout
import com.example.wasm2class.android.kotlin.lib.lib1.HelloworldLib
import com.example.wasm2class.android.kotlin.lib.lib1.StagingClockLib

public class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(FrameLayout(this))
        Thread(Companion::testWasm2class).start()
    }

    companion object {
        private fun testWasm2class() {
            HelloworldLib.runHelloWorld()
            StagingClockLib.runClock()
        }
    }
}
