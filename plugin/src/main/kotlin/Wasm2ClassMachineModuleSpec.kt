/*
 * SPDX-FileCopyrightText: 2025 Alexey Illarionov and the wasm2class-gradle-plugin project contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package at.released.wasm2class

import org.gradle.api.Named
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity.NONE
import org.gradle.kotlin.dsl.property
import javax.inject.Inject

@Wasm2ClassGeneratorDsl
public open class Wasm2ClassMachineModuleSpec @Inject constructor(
    private val name: String,
    objects: ObjectFactory,
    baseTargetPackage: Provider<String>,
) : Named {
    /**
     * The WASM binary file to be compiled.
     */
    @get:InputFile
    @get:PathSensitive(NONE)
    public val wasm: RegularFileProperty = objects.fileProperty()

    /**
     * The root package for the generated classes.
     */
    @get:Input
    public val targetPackage: Property<String> = objects.property<String>().convention(baseTargetPackage)

    /**
     * The name of the Module class.
     */
    @get:Input
    public val moduleClassSimpleName: Property<String> = objects.property<String>().convention(
        "${name}Module".toUpperCamelCase(),
    )

    /**
     * The name of the Machine class.
     */
    @get:Input
    public val machineClassSimpleName: Property<String> = objects.property<String>().convention(
        "${name}Machine".toUpperCamelCase(),
    )

    /**
     * The name of the generated stripped WASM resource.
     */
    @get:Input
    public val wasmMetaResourceName: Property<String> = objects.property<String>().convention(
        "${name.toUpperCamelCase()}.meta",
    )

    /**
     * Base name for the Machine and Module classes
     */
    @Input
    override fun getName(): String = name
}
