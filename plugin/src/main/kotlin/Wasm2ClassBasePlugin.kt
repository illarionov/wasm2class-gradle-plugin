/*
 * SPDX-FileCopyrightText: 2025 Alexey Illarionov and the wasm2class-gradle-plugin project contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package at.released.wasm2class

import at.released.wasm2class.Wasm2ClassConstants.Configurations.CHICORY_COMPILER
import at.released.wasm2class.Wasm2ClassConstants.Deps
import at.released.wasm2class.Wasm2ClassConstants.WASM2CLASS_EXTENSION_NAME
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.attributes.Usage
import org.gradle.kotlin.dsl.named

public class Wasm2ClassBasePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.extensions.create(WASM2CLASS_EXTENSION_NAME, Wasm2ClassExtension::class.java)

        val chicoryBuildTimeDependencies = target.configurations.create(CHICORY_COMPILER) {
            isCanBeResolved = false
            isCanBeConsumed = false
            isVisible = false
            description = "The classpath for the Chicory build time compiler"
            defaultDependencies {
                add(target.dependencies.create(Deps.CHICORY_BUILD_TIME_COMPILER))
            }
        }

        target.configurations.create(Wasm2ClassConstants.Configurations.CHICORY_COMPILER_RUNTIME_CLASSPATH) {
            isCanBeResolved = true
            isCanBeConsumed = false
            extendsFrom(chicoryBuildTimeDependencies)
            attributes {
                attribute(Usage.USAGE_ATTRIBUTE, target.objects.named(Usage.JAVA_RUNTIME))
            }
        }
    }
}
