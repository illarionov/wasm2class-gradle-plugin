/*
 * SPDX-FileCopyrightText: 2025 Alexey Illarionov and the wasm2class-gradle-plugin project contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package at.released.wasm2class

import org.gradle.api.Named
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity.NONE
import org.gradle.kotlin.dsl.property
import org.gradle.kotlin.dsl.setProperty
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
     * Fully qualified name for the generated class
     *
     * Default: "[targetPackage].[name.capitalized()][name]"
     */
    @get:Input
    public val outputClassPrefix: Property<String> = objects.property<String>().convention(
        targetPackage.map { "$it.${getName().capitalizeAscii()}" },
    )

    /**
     * The interpreter fallback to be used
     */
    @get:Input
    public val interpreterFallback: Property<InterpreterFallback> = objects.property<InterpreterFallback>().convention(
        InterpreterFallback.FAIL,
    )

    /**
     * The set of interpreted functions
     */
    @get:Input
    public val interpretedFunctions: SetProperty<Int> = objects.setProperty<Int>().convention(emptySet())

    @Input
    override fun getName(): String = name
}
