/*
 * SPDX-FileCopyrightText: 2025 Alexey Illarionov and the wasm2class-gradle-plugin project contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package at.released.wasm2class

import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.ProjectLayout
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.property
import javax.inject.Inject

@Wasm2ClassGeneratorDsl
public abstract class Wasm2ClassExtension @Inject constructor(
    objects: ObjectFactory,
    layout: ProjectLayout,
    project: Project,
) {
    public val modules: NamedDomainObjectContainer<Wasm2ClassMachineModuleSpec> = objects.domainObjectContainer(
        Wasm2ClassMachineModuleSpec::class.java,
    ) { name ->
        objects.newInstance(Wasm2ClassMachineModuleSpec::class.java, name).also {
            it.targetPackage.convention(this.targetPackage)
        }
    }

    /**
     * The root package for the generated classes.
     */
    public val targetPackage: Property<String> = objects.property<String>()
        .convention(project.provider { project.group.toString() })

    /**
     * The root directory for the generated classes.
     */
    public val outputDirectory: DirectoryProperty = objects.directoryProperty().convention(
        layout.buildDirectory.dir("generated-chicory-aot"),
    )

    /**
     * The output directory for the generated Java .class files.
     */
    public val outputClasses: DirectoryProperty = objects.directoryProperty().convention(
        outputDirectory.map { it.dir("classes") },
    )

    /**
     *  The output directory for the generated Java source files.
     */
    public val outputSources: DirectoryProperty = objects.directoryProperty().convention(
        outputDirectory.map { it.dir("sources") },
    )

    /**
     *  The output directory for the generated resources.
     */
    public val outputResources: DirectoryProperty = objects.directoryProperty().convention(
        outputDirectory.map { it.dir("resources") },
    )
}
