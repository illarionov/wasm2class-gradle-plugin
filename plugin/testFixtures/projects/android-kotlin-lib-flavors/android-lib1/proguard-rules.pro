#
# SPDX-FileCopyrightText: 2025 Alexey Illarionov and the wasm2class-gradle-plugin project contributors
# SPDX-License-Identifier: Apache-2.0
#

-keepattributes SourceFile,LineNumberTable
-dontobfuscate

-dontwarn java.lang.invoke.StringConcatFactory

-keep public class com.example.wasm2class.android.kotlin.lib.lib1.*Lib {
    public protected *;
}

-keepclasseswithmembers,allowoptimization public final class **Module {
    public static com.dylibso.chicory.wasm.WasmModule load();
}
