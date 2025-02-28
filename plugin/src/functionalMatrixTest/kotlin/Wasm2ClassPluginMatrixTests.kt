/*
 * SPDX-FileCopyrightText: 2025 Alexey Illarionov and the wasm2class-gradle-plugin project contributors
 * SPDX-License-Identifier: Apache-2.0
 */

import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.containsMatch
import at.released.wasm2class.test.functional.TestFixtures
import at.released.wasm2class.test.functional.junit.GradleTestProjectExtension
import at.released.wasm2class.test.functional.testmatrix.TestMatrix
import at.released.wasm2class.test.functional.testmatrix.VersionCatalog
import org.junit.jupiter.api.extension.RegisterExtension
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

class Wasm2ClassPluginMatrixTests {
    @JvmField
    @RegisterExtension
    var projectBuilder = GradleTestProjectExtension()

    @ParameterizedTest
    @MethodSource("javaPluginTestVariants")
    fun `can build the project with the wasm2class plugin and java application module`(versionCatalog: VersionCatalog) {
        projectBuilder.setupTestProject {
            versions = versionCatalog
            templateSubproject(TestFixtures.Projects.appJava)
        }

        projectBuilder.build("assemble").let { assembleResult ->
            assertThat(assembleResult.output).contains("BUILD SUCCESSFUL")
        }

        projectBuilder.build("app-java:run").let { runResult ->
            assertThat(runResult.output).contains("Hello, World!")
            assertThat(runResult.output).containsMatch("""Time: \d+""".toRegex())
        }
    }

    public companion object {
        @JvmStatic
        fun javaPluginTestVariants(): List<VersionCatalog> {
            return TestMatrix().getMainTestVariants()
                .groupingBy { it.gradleVersion }
                .reduce { _, first, _ -> first }
                .values
                .toList()
        }

        @JvmStatic
        fun mainTestVariants(): List<VersionCatalog> = TestMatrix().getMainTestVariants()
    }
}
