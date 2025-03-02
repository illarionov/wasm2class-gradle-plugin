/*
 * SPDX-FileCopyrightText: 2025 Alexey Illarionov and the wasm2class-gradle-plugin project contributors
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Suppress("UnstableApiUsage")

package at.released.wasm2class

import at.released.wasm2class.Wasm2ClassConstants.Deps
import at.released.wasm2class.Wasm2ClassConstants.WASM2CLASS_EXTENSION_NAME
import at.released.wasm2class.Wasm2ClassTask.GenerateChicoryMachineClasses.Companion.registerWasm2ClassTaskBase
import com.android.build.api.AndroidPluginVersion
import com.android.build.api.artifact.ScopedArtifact.CLASSES
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.api.variant.DslExtension
import com.android.build.api.variant.ScopedArtifacts.Scope.PROJECT
import com.android.build.api.variant.Variant
import com.android.build.api.variant.VariantExtension
import com.android.build.api.variant.VariantExtensionConfig
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.tasks.TaskProvider
import org.gradle.jvm.tasks.Jar
import java.util.SortedMap
import java.util.TreeMap

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

    // Configure global Wasm2ClassExtension with android-specific defaults
    val wasm2ClassExtension = extensions.getByType(Wasm2ClassExtension::class.java).apply {
        outputDirectory.convention(layout.buildDirectory.dir("generated-chicory-aot/android"))
        targetPackage.convention(extensions.getByType(CommonExtension::class.java).namespace)
    }

    // Register variant scoped extensions for Android build types and product flavors
    componentsExtension.registerExtension(
        DslExtension.Builder(WASM2CLASS_EXTENSION_NAME)
            .extendBuildTypeWith(Wasm2ClassVariantExtension::class.java)
            .extendProductFlavorWith(Wasm2ClassVariantExtension::class.java)
            .build(),
        ExtensionMerger(objects, wasm2ClassExtension),
    )

    // Setup variant tasks
    componentsExtension.onVariants { variant: Variant ->
        val variantExtension = checkNotNull(variant.getExtension(Wasm2ClassVariantExtension::class.java)) {
            "Extension not registered"
        }

        val wasm2ClassTask: TaskProvider<Wasm2ClassTask> = project.registerWasm2ClassTaskBase(
            "${variant.name}PrecompileWasm2class",
        ) {
            modules.set(variantExtension.modules)
        }

        variant.sources.java?.addGeneratedSourceDirectory(wasm2ClassTask, Wasm2ClassTask::outputSources)
        variant.sources.resources?.addGeneratedSourceDirectory(wasm2ClassTask, Wasm2ClassTask::outputResources)
        variant.artifacts.forScope(PROJECT).use(wasm2ClassTask).toAppend(CLASSES, Wasm2ClassTask::outputClasses)

        // To add generated .class files to the compile classpath, we first need to package them into a JAR archive.
        // There might be a simpler way to do this.
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

internal class ExtensionMerger(
    private val objects: ObjectFactory,
    private val globalExtension: Wasm2ClassExtension,
) : (VariantExtensionConfig<out Variant>) -> VariantExtension {
    override fun invoke(
        variantExtensions: VariantExtensionConfig<out Variant>,
    ): Wasm2ClassVariantExtension {
        val modulesMap: SortedMap<String, Wasm2ClassMachineModuleSpec> = TreeMap()

        globalExtension.modules.forEach { modulesMap[it.name] = it }
        variantExtensions.buildTypeExtension(Wasm2ClassVariantExtension::class.java).let {
            addVariantModules(modulesMap, it.modules)
        }
        variantExtensions.productFlavorsExtensions(Wasm2ClassVariantExtension::class.java).reversed().forEach {
            addVariantModules(modulesMap, it.modules)
        }

        return objects.newInstance(Wasm2ClassVariantExtension::class.java).apply {
            this.modules.addAll(modulesMap.values)
        }
    }

    private fun addVariantModules(
        map: MutableMap<String, Wasm2ClassMachineModuleSpec>,
        variantModules: NamedDomainObjectContainer<Wasm2ClassMachineModuleSpec>,
    ) {
        variantModules.forEach { variantModule ->
            val lowPrioModule = map[variantModule.name]
            map[variantModule.name] = if (lowPrioModule != null) {
                mergeModuleSpecs(variantModule, lowPrioModule)
            } else {
                variantModule
            }
        }
    }

    private fun mergeModuleSpecs(
        high: Wasm2ClassMachineModuleSpec,
        low: Wasm2ClassMachineModuleSpec,
    ): Wasm2ClassMachineModuleSpec = objects.newInstance(
        Wasm2ClassMachineModuleSpec::class.java,
        high.name,
    ).apply {
        wasm.set(high.wasm.orElse(low.wasm))
        targetPackage.set(high.targetPackage.orElse(low.targetPackage))
        moduleClassSimpleName.set(high.moduleClassSimpleName)
        machineClassSimpleName.set(high.machineClassSimpleName)
        wasmMetaResourceName.set(high.wasmMetaResourceName)
    }
}
