/*
 * SPDX-FileCopyrightText: 2025 Alexey Illarionov and the wasm2class-gradle-plugin project contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package at.released.wasm2class.test.functional.testproject

import at.released.wasm2class.test.functional.FileContent
import at.released.wasm2class.test.functional.TestFixtures
import at.released.wasm2class.test.functional.TestFixtures.Projects.SubprojectTemplateId
import at.released.wasm2class.test.functional.testmatrix.VersionCatalog
import at.released.wasm2class.test.functional.testproject.RootTestProject.AppliedPlugin.WASM2CLASS
import at.released.wasm2class.test.functional.testproject.RootTestProject.Builder.RootProjectFile.Subproject
import at.released.wasm2class.test.functional.util.androidHome
import java.nio.file.Path

public class RootTestProject private constructor(
    public override val rootDir: Path,
    public val versions: VersionCatalog,
    private val subprojects: Map<String, TestGradleProject>,
) : TestGradleProject {
    override val name: String = ":"

    public fun subproject(
        name: String,
    ): TestGradleProject = subprojects[name] ?: error("Subproject $name not found")

    public class Builder internal constructor(
        private val root: Path,
    ) : TestGradleProject.Builder<RootTestProject> {
        public var versions: VersionCatalog = VersionCatalog.getDefault()
        public var plugins: Set<AppliedPlugin> = setOf(WASM2CLASS)

        private val projectFiles: MutableMap<String, RootProjectFile> = mutableMapOf()

        public inline fun file(dstPath: String, block: StringBuilder.() -> Unit) {
            file(FileContent(dstPath, buildString(block)))
        }

        public fun file(file: FileContent) {
            projectFiles[file.dstPath] = RootProjectFile.File(file)
        }

        public fun subproject(
            name: String = "test",
            block: BasicTestGradleSubproject.Builder.() -> Unit = {},
        ) {
            projectFiles[name] = Subproject(
                BasicTestGradleSubproject.Builder(
                    rootDir = root.resolve(name),
                    name = name,
                ).apply(block),
            )
        }

        public fun templateSubproject(
            template: SubprojectTemplateId,
            block: BasicTestGradleSubproject.Builder.() -> Unit = {},
        ) {
            val templatePath = TestFixtures.Projects.rootPath.resolve(template.srcPath)

            val builder = BasicTestGradleSubproject.Builder(
                rootDir = root.resolve(template.projectName),
                name = template.projectName,
                templateDir = templatePath,
            ).apply {
                block()
            }

            projectFiles[template.projectName] = Subproject(builder)
        }

        override fun build(): RootTestProject {
            val versionsFile = versions.toLibsVersionsToml()
            val includes: List<String> = projectFiles.mapNotNull { (name, file) ->
                if (file is Subproject) name else null
            }
            val buildGradleFile = buildGradleKts(plugins = plugins.toTypedArray())
            val settingsFile = settingsGradleKts(includeSubprojects = includes.toTypedArray())
            root.writeFiles(versionsFile, buildGradleFile, settingsFile)

            val subprojects = projectFiles.mapNotNull { (_, fileOrSubproject) ->
                when (fileOrSubproject) {
                    is RootProjectFile.File -> {
                        root.writeFiles(fileOrSubproject.content)
                        null
                    }

                    is RootProjectFile.Subproject -> fileOrSubproject.builder.build()
                }
            }

            return RootTestProject(
                root,
                versions,
                subprojects.associateBy(TestGradleProject::name),
            )
        }

        private sealed class RootProjectFile {
            class File(val content: FileContent) : RootProjectFile()
            class Subproject(val builder: TestGradleProject.Builder<*>) : RootProjectFile()
        }
    }

    public enum class AppliedPlugin(public val libsTomlAlias: String?) {
        ANDROID_APPLICATION("libs.plugins.android.application"),
        ANDROID_LIBRARY("libs.plugins.android.library"),
        KOTLIN_ANDROID("libs.plugins.kotlin.android"),
        KOTLIN_JVM("libs.plugins.kotlin.jvm"),
        KOTLIN_MULTIPLATFORM("libs.plugins.kotlin.multiplatform"),
        WASM2CLASS("libs.plugins.wasm2class"),
    }

    public companion object {
        public operator fun invoke(
            rootDir: Path,
            block: Builder.() -> Unit = {},
        ): RootTestProject = Builder(rootDir).apply {
            listOf(
                localProperties(),
                gradleProperties(),
                TestFixtures.CommonFiles.clockWasm,
                TestFixtures.CommonFiles.helloworldWasm,
            ).forEach(::file)
            block()
        }.build()

        public fun gradleProperties(): FileContent {
            return FileContent(
                dstPath = "gradle.properties",
                content = """
                org.gradle.jvmargs=-Xmx2G -XX:MaxMetaspaceSize=768M -Dfile.encoding=UTF-8
                org.gradle.workers.max=2
                org.gradle.vfs.watch=false
                org.gradle.parallel=false
                org.gradle.caching=true
                org.gradle.configuration-cache=true
                android.useAndroidX=true
                android.nonTransitiveRClass=true
            """.trimIndent(),
            )
        }

        public fun localProperties(): FileContent {
            return FileContent(
                dstPath = "local.properties",
                content = "sdk.dir=${androidHome()}".trimIndent(),
            )
        }

        public fun settingsGradleKts(
            vararg includeSubprojects: String,
        ): FileContent {
            val newContent = buildString {
                """
                dependencyResolutionManagement {
                    repositories {
                        google()
                        mavenCentral()
                    }
                }
                """.trimIndent().let(::appendLine)

                includeSubprojects.map { "include(\"$it\")" }.forEach(::appendLine)

                """
                pluginManagement {
                    repositories {
                        exclusiveContent {
                            forRepository {
                                maven { url = uri("file://${TestFixtures.functionalTestsMaven}") }
                            }
                            filter {
                                includeGroupAndSubgroups("at.released.wasm2class")
                            }
                        }
                        google()
                        mavenCentral()
                        gradlePluginPortal()
                    }
                }
                """.trimIndent().let(::appendLine)
            }
            return FileContent("settings.gradle.kts", newContent)
        }

        public fun buildGradleKts(
            vararg plugins: AppliedPlugin,
        ): FileContent {
            val pluginsText = plugins
                .mapNotNull(AppliedPlugin::libsTomlAlias)
                .joinToString("\n") { "alias($it) apply false" }

            return FileContent(
                dstPath = "build.gradle.kts",
                content = "plugins {\n $pluginsText\n}".trimIndent(),
            )
        }
    }
}
