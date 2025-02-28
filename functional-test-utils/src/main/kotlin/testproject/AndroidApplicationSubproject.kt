/*
 * SPDX-FileCopyrightText: 2025 Alexey Illarionov and the wasm2class-gradle-plugin project contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package at.released.wasm2class.test.functional.testproject

import at.released.wasm2class.test.functional.FileContent
import at.released.wasm2class.test.functional.TestFixtures
import java.nio.file.Path
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.copyToRecursively

public class AndroidApplicationSubproject(
    public override val rootDir: Path,
    public override val name: String,
    public val androidNamespace: String,
) : TestGradleProject {
    public val AndroidApplicationSubproject.apkDir: Path
        get() = rootDir.resolve("build/outputs/apk/")

    @OptIn(ExperimentalPathApi::class)
    public class Builder internal constructor(
        public val rootDir: Path,
        public val name: String,
        public val androidNamespace: String,
    ) : TestGradleProject.Builder<AndroidApplicationSubproject> {
        public var template: Path? = null
        private val projectFiles: MutableMap<String, FileContent> = mutableMapOf()

        public fun add(file: FileContent) {
            projectFiles[file.dstPath] = file
        }

        public fun addAndroidManifestXml(): Unit = add(androidManifestXml())

        public fun addApplicationKt(): Unit = add(applicationKt(androidNamespace))

        public fun addBuildGradleKts(
            additionalText: String = "",
        ): Unit = add(buildGradleKts(androidNamespace, additionalText))

        public override fun build(): AndroidApplicationSubproject {
            template?.copyToRecursively(rootDir, overwrite = true, followLinks = false)
            rootDir.writeFiles(files = projectFiles.values.toTypedArray())
            return AndroidApplicationSubproject(rootDir, name, androidNamespace)
        }
    }

    public companion object {
        public operator fun invoke(
            rootDir: Path,
            name: String,
            androidNamespace: String,
            block: Builder.() -> Unit = {},
        ): AndroidApplicationSubproject = Builder(rootDir, name, androidNamespace).apply(block).build()

        public fun androidManifestXml(): FileContent {
            return TestFixtures.CommonFiles.file("submodule/src/main/AndroidManifest.xml")
        }

        public fun applicationKt(
            namespace: String,
        ): FileContent {
            val dstPath = "src/main/kotlin/${namespace.namespaceToPackage()}/Application.kt"
            val content = """
            package $namespace

            import android.app.Application

            class Application : Application() {
                override fun onCreate() {
                    super.onCreate()
                }
            }
        """.trimIndent()
            return FileContent(dstPath, content)
        }

        public fun buildGradleKts(
            namespace: String,
            additionalText: String = "",
        ): FileContent {
            val template = TestFixtures.CommonFiles.file("submodule/build.gradle.kts")
            val text = template.contentString.replace("<<NAMESPACE>>", namespace)

            return FileContent(
                template.dstPath,
                text + additionalText,
            )
        }

        private fun String.namespaceToPackage(): String = this.replace('.', '/')
    }
}
