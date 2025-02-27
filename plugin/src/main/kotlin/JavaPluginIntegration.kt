/*
 * SPDX-FileCopyrightText: 2025 Alexey Illarionov and the wasm2class-gradle-plugin project contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package at.released.wasm2class

import at.released.wasm2class.Wasm2ClassConstants.Deps
import at.released.wasm2class.Wasm2ClassTask.GenerateChicoryMachineClasses.Companion.registerWasm2ClassTask
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension

internal fun Project.setupJavaPluginIntegration() {
    val wasm2ClassExtension: Wasm2ClassExtension = extensions.getByType(Wasm2ClassExtension::class.java)
    wasm2ClassExtension.outputDirectory.convention(layout.buildDirectory.dir("generated-chicory-aot/java"))

    val wasm2classTask = registerWasm2ClassTask()

    extensions.configure<JavaPluginExtension>("java") {
        sourceSets.named("jvmMain") {
            java.srcDir(wasm2classTask.flatMap(Wasm2ClassTask::outputSources))
            resources.srcDir(wasm2classTask.flatMap(Wasm2ClassTask::outputResources))
            resources.srcDir(wasm2classTask.flatMap(Wasm2ClassTask::outputClasses))
        }
    }

    val outputClasses = wasm2classTask.flatMap(Wasm2ClassTask::outputClasses)
    with(dependencies) {
        add("compileOnly", files(outputClasses))
        add("api", Deps.CHICORY_RUNTIME)
    }
}
