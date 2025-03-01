/*
 * SPDX-FileCopyrightText: 2025 Alexey Illarionov and the wasm2class-gradle-plugin project contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package com.example.wasm2class.javalib.app;

import com.example.wasm2class.javalib.lib.Lib;

public class Main {
    public static void main(String[] args) {
        Lib.runHelloWorld();
        Lib.runClock();
    }
}
