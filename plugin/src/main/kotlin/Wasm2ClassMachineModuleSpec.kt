/*
 * SPDX-FileCopyrightText: 2025 Alexey Illarionov and the wasm2class-gradle-plugin project contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package at.released.wasm2class

import org.gradle.api.Named
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity.NONE
import org.gradle.kotlin.dsl.property
import javax.inject.Inject

@Wasm2ClassGeneratorDsl
public open class Wasm2ClassMachineModuleSpec @Inject constructor(
    private val name: String,
    objects: ObjectFactory,
) : Named {
    /**
     * The WASM binary file to be compiled.
     */
    @get:InputFile
    @get:PathSensitive(NONE)
    public val wasm: RegularFileProperty = objects.fileProperty()

    /**
     * The root package for the generated classes.
     *
     * Used in a pattern for [outputClassPrefix], allowing you to avoid specifying the full prefix
     *
     * Default: the group of the Gradle project or the target namespace on Android.
     */
    @get:Internal
    public val targetPackage: Property<String> = objects.property<String>()

    /**
     * The base name for generated classes in the format of `<target package>.<base name>`.
     * For example: *"com.example.Add"*. A class named *"com.example.AddModule"*
     * will be generated based on this name.
     *
     * Default: "[targetPackage].[name.capitalized()][name]"
     */
    @get:Input
    public val outputClassPrefix: Property<String> = objects.property<String>().convention(
        targetPackage.map { "$it.${getName().capitalizeAscii()}" },
    )

    @Input
    override fun getName(): String = name
}
