#
# SPDX-FileCopyrightText: 2025 Alexey Illarionov and the wasm2class-gradle-plugin project contributors
# SPDX-License-Identifier: Apache-2.0
#

-dontwarn com.google.errorprone.annotations.FormatMethod
-dontwarn java.lang.System$Logger$Level
-dontwarn java.lang.System$Logger

-if public final class ** { public static com.dylibso.chicory.wasm.WasmModule load(); }
-keepnames class <1>
