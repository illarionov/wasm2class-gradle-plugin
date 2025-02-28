/*
 * SPDX-FileCopyrightText: 2025 Alexey Illarionov and the wasm2class-gradle-plugin project contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package at.released.wasm2class

import assertk.assertThat
import assertk.assertions.contains
import at.released.wasm2class.test.functional.junit.GradleTestProjectExtension
import org.gradle.testkit.runner.BuildResult
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

class Wasm2ClassPluginTest {
    @JvmField
    @RegisterExtension
    var projectBuilder = GradleTestProjectExtension()

    @Test
    fun `test project build with plugin applied`() {
        projectBuilder.setupTestProject {
            subproject("test") {
                file("build.gradle.kts") {
                    append(
                        """
                            plugins {
                                `java`
                                alias(libs.plugins.wasm2class)
                            }
                            wasm2class {
                            }
                        """.trimIndent(),
                    )
                }
            }
        }
        val result: BuildResult = projectBuilder.build("assemble")

        assertThat(result.output).contains("BUILD SUCCESSFUL")
    }
}
