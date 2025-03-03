/*
 * SPDX-FileCopyrightText: 2025 Alexey Illarionov and the wasm2class-gradle-plugin project contributors
 * SPDX-License-Identifier: Apache-2.0
 */

import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.containsMatch
import at.released.wasm2class.test.functional.TestFixtures
import at.released.wasm2class.test.functional.apk.assertThatApk
import at.released.wasm2class.test.functional.apk.hasGeneratedAotFiles
import at.released.wasm2class.test.functional.junit.GradleTestProjectExtension
import at.released.wasm2class.test.functional.testmatrix.TestMatrix
import at.released.wasm2class.test.functional.testmatrix.VersionCatalog
import at.released.wasm2class.test.functional.testproject.RootTestProject.AppliedPlugin.ANDROID_APPLICATION
import at.released.wasm2class.test.functional.testproject.RootTestProject.AppliedPlugin.ANDROID_LIBRARY
import at.released.wasm2class.test.functional.testproject.RootTestProject.AppliedPlugin.KOTLIN_ANDROID
import at.released.wasm2class.test.functional.testproject.RootTestProject.AppliedPlugin.KOTLIN_JVM
import at.released.wasm2class.test.functional.testproject.RootTestProject.AppliedPlugin.KOTLIN_MULTIPLATFORM
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
    fun `can build java application`(versionCatalog: VersionCatalog) {
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
    fun `can build java library module`(versionCatalog: VersionCatalog) {
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
    @MethodSource("kotlinPluginTestVariants")
    fun `can build kotlin application`(versionCatalog: VersionCatalog) {
        projectBuilder.setupTestProject {
            versions = versionCatalog
            plugins = setOf(WASM2CLASS, KOTLIN_JVM)
            templateSubproject(TestFixtures.Projects.kotlinApp)
        }

        projectBuilder.build("build").let { assembleResult ->
            assertThat(assembleResult.output).contains("BUILD SUCCESSFUL")
        }

        projectBuilder.build("run").let { runResult ->
            assertThat(runResult.output).contains("Hello, World!")
            assertThat(runResult.output).containsMatch("""Time: \d+""".toRegex())
        }
    }

    @ParameterizedTest
    @MethodSource("androidJavaPluginTestVariants")
    fun `can build java android application`(versionCatalog: VersionCatalog) {
        val androidJavaApp = TestFixtures.Projects.androidJavaApp
        val root = projectBuilder.setupTestProject {
            versions = versionCatalog
            plugins = setOf(WASM2CLASS, ANDROID_APPLICATION)
            templateSubproject(androidJavaApp)
        }

        projectBuilder.build("build").let { assembleResult ->
            assertThat(assembleResult.output).contains("BUILD SUCCESSFUL")
        }

        val debugApkPath = root.subproject(androidJavaApp.projectName).rootDir.resolve(
            "build/outputs/apk/debug/android-java-app-debug.apk",
        )
        assertThatApk(debugApkPath) {
            listOf("Helloworld", "Clock").forEach {
                hasGeneratedAotFiles("com.example.wasm2class.android.java.app", it)
            }
        }
    }

    @ParameterizedTest
    @MethodSource("allTestVariants")
    fun `can build the kotlin android library with flavors`(versionCatalog: VersionCatalog) {
        val androidFlavorsApp = TestFixtures.Projects.androidKotlinLibFlavorsApp
        val root = projectBuilder.setupTestProject {
            versions = versionCatalog
            plugins = setOf(WASM2CLASS, ANDROID_APPLICATION, ANDROID_LIBRARY, KOTLIN_ANDROID)
            templateSubproject(androidFlavorsApp)
            templateSubproject(TestFixtures.Projects.androidKotlinLibFlavorsLib1)
        }

        projectBuilder.build("build").let { assembleResult ->
            assertThat(assembleResult.output).contains("BUILD SUCCESSFUL")
        }

        val debugApkPath = root.subproject(androidFlavorsApp.projectName).rootDir.resolve(
            "build/outputs/apk/full/staging/android-app-full-staging.apk",
        )
        assertThatApk(debugApkPath) {
            hasGeneratedAotFiles("com.example.wasm2class.android.kotlin.lib.lib1", "Helloworld")
            hasGeneratedAotFiles("com.example.wasm2class.android.kotlin.lib.lib1.clock", "Clock")
        }
    }

    @ParameterizedTest
    @MethodSource("allTestVariants")
    fun `can build the multiplatform project with jvm and android targets`(versionCatalog: VersionCatalog) {
        val app = TestFixtures.Projects.kotlinMultiplatformApp
        val root = projectBuilder.setupTestProject {
            versions = versionCatalog
            plugins = setOf(WASM2CLASS, ANDROID_APPLICATION, KOTLIN_MULTIPLATFORM)
            templateSubproject(app)
        }

        projectBuilder.build("build").let { assembleResult ->
            assertThat(assembleResult.output).contains("BUILD SUCCESSFUL")
        }

        val apkPath = root.subproject(app.projectName).rootDir.resolve(
            "build/outputs/apk/debug/kmp-jvm-android-app-debug.apk",
        )
        assertThatApk(apkPath) {
            listOf("Helloworld", "Clock").forEach {
                hasGeneratedAotFiles("com.example.wasm2class.kmp", it)
            }
        }

        projectBuilder.build("jvmRun").let { runResult ->
            assertThat(runResult.output).contains("Hello, World!")
            assertThat(runResult.output).containsMatch("""Time: \d+""".toRegex())
        }
    }

    public companion object {
        @JvmStatic
        fun javaPluginTestVariants(): List<VersionCatalog> = testVariantsDistinctBy { it.gradleVersion }

        @JvmStatic
        fun androidJavaPluginTestVariants(): List<VersionCatalog> = testVariantsDistinctBy {
            it.gradleVersion to it.agpVersion
        }

        @JvmStatic
        fun kotlinPluginTestVariants(): List<VersionCatalog> = testVariantsDistinctBy {
            it.gradleVersion to it.kotlinVersion
        }

        fun <K> testVariantsDistinctBy(groupingBy: (VersionCatalog) -> K): List<VersionCatalog> {
            return TestMatrix().getMainTestVariants()
                .groupingBy(groupingBy)
                .reduce { _, first, _ -> first }
                .values
                .toList()
        }

        @JvmStatic
        fun allTestVariants(): List<VersionCatalog> = TestMatrix().getMainTestVariants()
    }
}
