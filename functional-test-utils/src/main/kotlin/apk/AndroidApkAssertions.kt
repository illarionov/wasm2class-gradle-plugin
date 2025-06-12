/*
 * SPDX-FileCopyrightText: 2025 Alexey Illarionov and the wasm2class-gradle-plugin project contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package at.released.wasm2class.test.functional.apk

import assertk.Assert
import assertk.all
import assertk.assertThat
import assertk.assertions.isNotNull
import assertk.assertions.isRegularFile
import assertk.assertions.support.appendName
import java.nio.file.LinkOption.NOFOLLOW_LINKS
import java.nio.file.Path

private const val WASM_MODULE_LOAD_METHOD_SIGNATURE = "load()Lcom/dylibso/chicory/wasm/WasmModule;"
private const val WASM_MACHINE_INIT_METHOD_SIGNATURE = "<init>(Lcom/dylibso/chicory/runtime/Instance;)V"

public fun assertThatApk(
    path: Path,
    body: Assert<ApkInspector>.() -> Unit,
) {
    ApkInspector(path).use { apkInspector ->
        assertThat(apkInspector).all(body)
    }
}

public fun Assert<ApkInspector>.hasMethodBytecode(
    classFqcn: String,
    methodSignature: String? = "<clinit>()V",
): Assert<String> = transform { it.getDexCode(classFqcn, methodSignature) }.isNotNull()

public fun Assert<ApkInspector>.hasGeneratedAotFiles(
    targetPackage: String,
    moduleBaseName: String,
): Unit = all {
    val packageFqn = targetPackage.replace(".", "/")
    hasGeneratedModuleClassBytecode("$packageFqn/$moduleBaseName")
    hasGeneratedMachineClassBytecode("$packageFqn/${moduleBaseName}Machine")
    hasGeneratedResource(targetPackage, "$moduleBaseName.meta")
}

public fun Assert<ApkInspector>.hasGeneratedModuleClassBytecode(
    moduleFqn: String,
): Assert<String> = transform(name = appendName(".moduleClass[`$moduleFqn`]")) {
    it.getDexCode(moduleFqn, WASM_MODULE_LOAD_METHOD_SIGNATURE)
}.isNotNull()

public fun Assert<ApkInspector>.hasGeneratedMachineClassBytecode(
    machineFqn: String,
): Assert<String> = transform(name = appendName(".machineClass[`$machineFqn`]")) {
    it.getDexCode(machineFqn, WASM_MACHINE_INIT_METHOD_SIGNATURE)
}.isNotNull()

public fun Assert<ApkInspector>.hasGeneratedResource(
    targetPackage: String,
    resourceName: String,
): Unit = transform {
    it.contextRoot
        .resolve(targetPackage.replace(".", "/"))
        .resolve(resourceName)
}.isRegularFile(NOFOLLOW_LINKS)
