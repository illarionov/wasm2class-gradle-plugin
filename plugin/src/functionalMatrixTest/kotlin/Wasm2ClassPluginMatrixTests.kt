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
import at.released.wasm2class.test.functional.testproject.RootTestProject.AppliedPlugin.ANDROID_APPLICATION
import at.released.wasm2class.test.functional.testproject.RootTestProject.AppliedPlugin.ANDROID_LIBRARY
import at.released.wasm2class.test.functional.testproject.RootTestProject.AppliedPlugin.KOTLIN_ANDROID
import at.released.wasm2class.test.functional.testproject.RootTestProject.AppliedPlugin.WASM2CLASS
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
            templateSubproject(TestFixtures.Projects.javaApp)
        }

        projectBuilder.build("assemble").let { assembleResult ->
            assertThat(assembleResult.output).contains("BUILD SUCCESSFUL")
        }

        projectBuilder.build("run").let { runResult ->
            assertThat(runResult.output).contains("Hello, World!")
            assertThat(runResult.output).containsMatch("""Time: \d+""".toRegex())
        }
    }

    @ParameterizedTest
    @MethodSource("javaPluginTestVariants")
    fun `can build the project with the wasm2class plugin and java library module`(versionCatalog: VersionCatalog) {
        projectBuilder.setupTestProject {
            versions = versionCatalog
            templateSubproject(TestFixtures.Projects.javaLibApp)
            templateSubproject(TestFixtures.Projects.javaLibLib)
        }

        projectBuilder.build("assemble").let { assembleResult ->
            assertThat(assembleResult.output).contains("BUILD SUCCESSFUL")
        }

        projectBuilder.build("run").let { runResult ->
            assertThat(runResult.output).contains("Hello, World!")
            assertThat(runResult.output).containsMatch("""Time: \d+""".toRegex())
        }
    }

    @ParameterizedTest
    @MethodSource("androidJavaPluginTestVariants")
    fun `can build the java android application with the wasm2class plugin`(versionCatalog: VersionCatalog) {
        projectBuilder.setupTestProject {
            versions = versionCatalog
            plugins = setOf(WASM2CLASS, ANDROID_APPLICATION)
            templateSubproject(TestFixtures.Projects.androidJavaApp)
            templateSubproject(TestFixtures.Projects.javaLibLib)
        }

        projectBuilder.build("build").let { assembleResult ->
            assertThat(assembleResult.output).contains("BUILD SUCCESSFUL")
        }

        // TODO: inspect debug APK
    }

    @ParameterizedTest
    @MethodSource("allTestVariants")
    fun `can build the kotlin android library with flavors`(versionCatalog: VersionCatalog) {
        projectBuilder.setupTestProject {
            versions = versionCatalog
            plugins = setOf(WASM2CLASS, ANDROID_APPLICATION, ANDROID_LIBRARY, KOTLIN_ANDROID)
            templateSubproject(TestFixtures.Projects.androidKotlinLibFlavorsApp)
            templateSubproject(TestFixtures.Projects.androidKotlinLibFlavorsLib1)
        }

        projectBuilder.build("build").let { assembleResult ->
            assertThat(assembleResult.output).contains("BUILD SUCCESSFUL")
        }

        // TODO: inspect debug APK
    }

    public companion object {
        @JvmStatic
        fun javaPluginTestVariants(): List<VersionCatalog> = mainTestVariants { it.gradleVersion }

        @JvmStatic
        fun androidJavaPluginTestVariants(): List<VersionCatalog> = mainTestVariants {
            it.gradleVersion to it.agpVersion
        }

        fun <K> mainTestVariants(groupingBy: (VersionCatalog) -> K): List<VersionCatalog> {
            return TestMatrix().getMainTestVariants()
                .groupingBy(groupingBy)
                .reduce { _, first, _ -> first }
                .values
                .toList()
        }

        @JvmStatic
        fun <K> allTestVariants(): List<VersionCatalog> = TestMatrix().getMainTestVariants()
    }
}
