/*
 * SPDX-FileCopyrightText: 2025 Alexey Illarionov and the wasm2class-gradle-plugin project contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package at.released.wasm2class.test.functional.junit

import at.released.wasm2class.test.functional.testmatrix.Version
import at.released.wasm2class.test.functional.testmatrix.VersionCatalog
import at.released.wasm2class.test.functional.testproject.RootTestProject
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.TestWatcher
import java.nio.file.Files
import java.nio.file.Path
import java.util.Optional
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.deleteRecursively

public class GradleTestProjectExtension : BeforeEachCallback, TestWatcher {
    private lateinit var rootDir: Path
    private var testProject: RootTestProject? = null

    override fun beforeEach(context: ExtensionContext?) {
        rootDir = Files.createTempDirectory("wasm2class-test")
    }

    override fun testSuccessful(context: ExtensionContext?) {
        cleanup()
    }

    override fun testAborted(context: ExtensionContext?, cause: Throwable?) {
        cleanup()
    }

    override fun testFailed(context: ExtensionContext?, cause: Throwable?) {
        // do not clean up, leave a temporary rootDir directory for future inspection
    }

    override fun testDisabled(context: ExtensionContext?, reason: Optional<String>?): Unit = Unit

    public fun setupTestProject(
        block: RootTestProject.Builder.() -> Unit,
    ): RootTestProject {
        return RootTestProject(rootDir, block).also {
            testProject = it
        }
    }

    public fun build(vararg args: String): BuildResult = buildWithGradleVersion(
        expectFail = false,
        args = args,
    )

    public fun buildAndFail(vararg args: String): BuildResult = buildWithGradleVersion(
        expectFail = true,
        args = args,
    )

    public fun buildWithGradleVersion(
        gradleVersion: Version? = null,
        expectFail: Boolean = false,
        vararg args: String,
    ): BuildResult {
        val testProject = this.testProject
        val realGradleVersion = when {
            gradleVersion != null -> gradleVersion
            testProject != null -> testProject.versions.gradleVersion
            else -> VersionCatalog.getDefault().gradleVersion
        }

        val runner = GradleRunner.create().apply {
            forwardOutput()
            withArguments(
                "--stacktrace",
                *args,
            )
            withProjectDir(rootDir.toFile())
            withGradleVersion(realGradleVersion.toString())
        }
        return if (!expectFail) {
            runner.build()
        } else {
            runner.buildAndFail()
        }
    }

    @OptIn(ExperimentalPathApi::class)
    private fun cleanup() {
        rootDir.deleteRecursively()
    }
}
