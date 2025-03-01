/*
 * SPDX-FileCopyrightText: 2025 Alexey Illarionov and the wasm2class-gradle-plugin project contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package at.released.wasm2class

import at.released.wasm2class.Wasm2ClassConstants.Deps
import at.released.wasm2class.Wasm2ClassTask.GenerateChicoryMachineClasses.Companion.registerWasm2ClassTask
import com.android.build.api.AndroidPluginVersion
import com.android.build.api.artifact.ScopedArtifact.CLASSES
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.api.variant.ScopedArtifacts.Scope.PROJECT
import com.android.build.api.variant.Variant
import org.gradle.api.Project
import org.gradle.api.tasks.TaskProvider
import org.gradle.jvm.tasks.Jar

internal fun Project.setupAndroidPluginIntegration() {
    val componentsExtension = extensions.findByType(AndroidComponentsExtension::class.java)
    checkNotNull(componentsExtension) {
        "Could not find the Android Gradle Plugin (AGP) extension"
    }
    @Suppress("MagicNumber")
    check(componentsExtension.pluginVersion >= AndroidPluginVersion(8, 0)) {
        "Wasm2class Gradle plugin is only compatible with Android Gradle plugin (AGP) " +
                "version 8.0 or higher (found ${componentsExtension.pluginVersion})."
    }

    val wasm2ClassExtension = extensions.getByType(Wasm2ClassExtension::class.java)
    wasm2ClassExtension.outputDirectory.convention(layout.buildDirectory.dir("generated-chicory-aot/android"))
    wasm2ClassExtension.targetPackage.convention(extensions.getByType(CommonExtension::class.java).namespace)

    componentsExtension.onVariants { variant: Variant ->
        val wasm2ClassTask: TaskProvider<Wasm2ClassTask> = project.registerWasm2ClassTask(
            name = "${variant.name}PrecompileWasm2class",
            wasm2ClassExtension = wasm2ClassExtension,
        )

        variant.sources.java?.addGeneratedSourceDirectory(wasm2ClassTask, Wasm2ClassTask::outputSources)
        variant.sources.resources?.addGeneratedSourceDirectory(wasm2ClassTask, Wasm2ClassTask::outputResources)
        variant.artifacts.forScope(PROJECT).use(wasm2ClassTask).toAppend(CLASSES, Wasm2ClassTask::outputClasses)

        // Unable to add classes to compile classpath without packaging to jar
        val packAotMachineClassesJar: TaskProvider<Jar> = tasks.register(
            "${variant.name}PackAotMachineJar",
            Jar::class.java,
        ) {
            from(wasm2ClassTask.flatMap(Wasm2ClassTask::outputClasses))
            destinationDirectory.set(
                wasm2ClassExtension.outputDirectory.map { it.dir("${variant.name}AotMachineClassesJar") },
            )
            archiveFileName.set("${variant.name}-aot-machine-classes.jar")
        }

        val aotMachineJar = "${variant.name}AotMachineJar"
        val aotMachineJarConfiguration = configurations.create(aotMachineJar) {
            isCanBeConsumed = false
            isCanBeResolved = false
            isVisible = false
        }
        dependencies.add(aotMachineJar, files(packAotMachineClassesJar.flatMap(Jar::getArchiveFile)))
        variant.compileConfiguration.extendsFrom(aotMachineJarConfiguration)
    }

    project.dependencies.add("api", Deps.CHICORY_RUNTIME)
}
