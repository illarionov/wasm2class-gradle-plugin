/*
 * SPDX-FileCopyrightText: 2025 Alexey Illarionov and the wasm2class-gradle-plugin project contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package at.released.wasm2class.test.functional.testproject

import at.released.wasm2class.test.functional.FileContent
import java.nio.file.Path
import kotlin.io.path.createParentDirectories
import kotlin.io.path.writeBytes

internal fun Path.writeFiles(vararg files: FileContent) {
    files.forEach {
        resolve(it.dstPath).createParentDirectories().writeBytes(it.content)
    }
}

public fun TestGradleProject.writeFiles(vararg files: FileContent): Unit = rootDir.writeFiles(files = files)
