/*
 * SPDX-FileCopyrightText: 2025 Alexey Illarionov and the wasm2class-gradle-plugin project contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package at.released.wasm2class.test.functional.testproject

import at.released.wasm2class.test.functional.FileContent
import java.nio.file.Path
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.copyToRecursively

public class BasicTestGradleSubproject(
    public override val rootDir: Path,
    public override val name: String,
) : TestGradleProject {

    @OptIn(ExperimentalPathApi::class)
    public class Builder internal constructor(
        public val rootDir: Path,
        public val name: String,
        public var template: Path? = null,
    ) : TestGradleProject.Builder<BasicTestGradleSubproject> {
        private val projectFiles: MutableMap<String, FileContent> = mutableMapOf()

        public inline fun file(dstPath: String, block: StringBuilder.() -> Unit) {
            file(FileContent(dstPath, buildString(block)))
        }

        public fun file(file: FileContent) {
            projectFiles[file.dstPath] = file
        }

        public override fun build(): BasicTestGradleSubproject {
            template?.copyToRecursively(rootDir, overwrite = true, followLinks = false)
            rootDir.writeFiles(files = projectFiles.values.toTypedArray())
            return BasicTestGradleSubproject(rootDir, name)
        }
    }

    public companion object {
        public operator fun invoke(
            rootDir: Path,
            name: String,
            block: Builder.() -> Unit = {},
        ): BasicTestGradleSubproject = Builder(rootDir, name).apply(block).build()
    }
}
