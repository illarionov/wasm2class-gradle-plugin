#
# SPDX-FileCopyrightText: 2025 Alexey Illarionov and the wasm2class-gradle-plugin project contributors
# SPDX-License-Identifier: Apache-2.0
#

-dontwarn com.dylibso.chicory.experimental.hostmodule.annotations.Buffer
-dontwarn com.dylibso.chicory.experimental.hostmodule.annotations.HostModule
-dontwarn com.dylibso.chicory.experimental.hostmodule.annotations.WasmExport
-dontwarn com.google.errorprone.annotations.FormatMethod
-dontwarn java.lang.System$Logger$Level
-dontwarn java.lang.System$Logger

-keepclasseswithmembers,allowoptimization public final class **Module {
    public static com.dylibso.chicory.wasm.WasmModule load();
}
