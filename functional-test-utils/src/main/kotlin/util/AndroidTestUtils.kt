/*
 * SPDX-FileCopyrightText: 2025 Alexey Illarionov and the wasm2class-gradle-plugin project contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package at.released.wasm2class.test.functional.util

// Original license:
/*
 * Copyright (C) 2016 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.File
import java.util.Properties

public fun androidHome(): String {
    val env = System.getenv("ANDROID_HOME")
    if (env != null) {
        return env.withInvariantPathSeparators()
    }
    val localProp = File(File(System.getProperty("user.dir")).parentFile, "local.properties")
    if (localProp.exists()) {
        val prop = Properties()
        localProp.inputStream().use {
            prop.load(it)
        }
        val sdkHome = prop.getProperty("sdk.dir")
        if (sdkHome != null) {
            return sdkHome.withInvariantPathSeparators()
        }
    }
    error("Missing 'ANDROID_HOME' environment variable or local.properties with 'sdk.dir'")
}

internal fun String.withInvariantPathSeparators() = replace("\\", "/")
