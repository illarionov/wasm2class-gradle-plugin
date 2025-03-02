/*
 * SPDX-FileCopyrightText: 2025 Alexey Illarionov and the wasm2class-gradle-plugin project contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package at.released.wasm2class

import at.released.wasm2class.Wasm2ClassConstants.Configurations.CHICORY_AOT_COMPILER_RUNTIME_CLASSPATH
import at.released.wasm2class.Wasm2ClassConstants.Deps
import at.released.wasm2class.Wasm2ClassConstants.Deps.CHICORY_GROUP
import at.released.wasm2class.Wasm2ClassTask.GenerateChicoryMachineClasses.Companion.registerWasm2ClassTask
import org.gradle.api.Project
import org.gradle.api.artifacts.component.ModuleComponentIdentifier
import org.gradle.api.file.Directory
import org.gradle.api.file.FileCollection
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.tasks.compile.JavaCompile
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation.Companion.MAIN_COMPILATION_NAME
import org.jetbrains.kotlin.gradle.targets.jvm.KotlinJvmTarget

internal fun Project.setupKotlinMultiplatformPluginIntegration() {
    val wasm2ClassExtension: Wasm2ClassExtension = extensions.getByType(Wasm2ClassExtension::class.java)
    wasm2ClassExtension.outputDirectory.convention(layout.buildDirectory.dir("generated-chicory-aot/multiplatform"))

    val wasm2classTask = registerWasm2ClassTask()

    val aotCompileClasspath: FileCollection = configurations.named(CHICORY_AOT_COMPILER_RUNTIME_CLASSPATH).get()
        .incoming
        .artifactView {
            componentFilter {
                it is ModuleComponentIdentifier &&
                        it.group == CHICORY_GROUP &&
                        (it.module == "runtime" || it.module == "wasm")
            }
        }
        .files

    extensions.configure<KotlinMultiplatformExtension>("kotlin") {
        targets.withType(KotlinJvmTarget::class.java) {
            compilations.named(MAIN_COMPILATION_NAME).configure {
                val compileAotModuleTask = registerCompileModuleTask(
                    this@withType,
                    wasm2classTask,
                    outputClassesDir = wasm2ClassExtension.outputDirectory.map { it.dir("module-classes") },
                    aotCompileClasspath = aotCompileClasspath,
                )

                val aotModuleClasses = compileAotModuleTask.flatMap(JavaCompile::getDestinationDirectory)
                val outputClasses = wasm2classTask.flatMap(Wasm2ClassTask::outputClasses)
                defaultSourceSet {
                    resources.srcDirs(
                        wasm2classTask.flatMap(Wasm2ClassTask::outputResources),
                        outputClasses,
                        aotModuleClasses,
                    )
                    dependencies {
                        api(Deps.CHICORY_RUNTIME)
                        compileOnly(files(aotModuleClasses, outputClasses))
                    }
                }
            }
        }
    }
}

private fun Project.registerCompileModuleTask(
    target: KotlinJvmTarget,
    wasm2ClassTask: TaskProvider<Wasm2ClassTask>,
    outputClassesDir: Provider<Directory>,
    aotCompileClasspath: FileCollection,
): Provider<JavaCompile> {
    return tasks.register("${target.name}CompileAotModuleWithJavac", JavaCompile::class.java) {
        source(wasm2ClassTask.flatMap(Wasm2ClassTask::outputSources))
        classpath = files(
            aotCompileClasspath,
            wasm2ClassTask.flatMap(Wasm2ClassTask::outputClasses),
        )
        destinationDirectory.set(outputClassesDir)
        dependsOn(wasm2ClassTask)
    }
}
