/*
 * SPDX-FileCopyrightText: 2025 Alexey Illarionov and the wasm2class-gradle-plugin project contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package at.released.wasm2class.test.functional.testproject

import java.nio.file.Path

public interface TestGradleProject {
    public val name: String
    public val rootDir: Path

    public fun interface Builder<P : TestGradleProject> {
        public fun build(): P
    }
}
