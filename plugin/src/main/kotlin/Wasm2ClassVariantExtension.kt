/*
 * SPDX-FileCopyrightText: 2025 Alexey Illarionov and the wasm2class-gradle-plugin project contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package at.released.wasm2class

import com.android.build.api.variant.VariantExtension
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.model.ObjectFactory
import javax.inject.Inject

@Wasm2ClassGeneratorDsl
public abstract class Wasm2ClassVariantExtension @Inject constructor(
    objects: ObjectFactory,
) : VariantExtension {
    public val modules: NamedDomainObjectContainer<Wasm2ClassMachineModuleSpec> =
        objects.domainObjectContainer(Wasm2ClassMachineModuleSpec::class.java)
}
