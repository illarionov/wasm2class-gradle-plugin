/*
 * SPDX-FileCopyrightText: 2025 Alexey Illarionov and the wasm2class-gradle-plugin project contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package at.released.wasm2class

import at.released.wasm2class.Wasm2ClassTask.GenerateChicoryMachineClasses.Companion.registerWasm2ClassTask
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation.Companion.MAIN_COMPILATION_NAME
import org.jetbrains.kotlin.gradle.targets.jvm.KotlinJvmTarget

internal fun Project.setupKotlinMultiplatformPluginIntegration() {
    val wasm2ClassExtension: Wasm2ClassExtension = extensions.getByType(Wasm2ClassExtension::class.java)
    wasm2ClassExtension.outputDirectory.convention(layout.buildDirectory.dir("generated-chicory-aot/multiplatform"))

    val wasm2classTask = registerWasm2ClassTask()

    extensions.configure<KotlinMultiplatformExtension>("kotlin") {
        targets.withType(KotlinJvmTarget::class.java) {
            compilations.named(MAIN_COMPILATION_NAME).configure {
                compileJavaTaskProvider?.configure {
                    source(wasm2classTask.flatMap(Wasm2ClassTask::outputSources))
                }

                val outputClasses = wasm2classTask.flatMap(Wasm2ClassTask::outputClasses)
                defaultSourceSet {
                    resources.srcDir(wasm2classTask.flatMap(Wasm2ClassTask::outputResources))
                    resources.srcDir(outputClasses)
                    dependencies {
                        api(Wasm2ClassConstants.Deps.CHICORY_RUNTIME)
                        compileOnly(files(outputClasses))
                    }
                }
            }
        }
    }
}
