/*
 * SPDX-FileCopyrightText: 2025 Alexey Illarionov and the wasm2class-gradle-plugin project contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package at.released.wasm2class.test.functional.util

public fun getApkPath(
    appName: String,
    buildType: String,
    vararg flavors: String,
): String {
    val flavorsFullNameCamelCase = flavors.reduceOrNull { name, subFlavor -> name + subFlavor.capitalized() }

    val apkName = buildList {
        add(appName)
        addAll(flavors)
        add(buildType)
        if (buildType != "debug") {
            add("unsigned")
        }
    }.joinToString("-")

    return if (flavorsFullNameCamelCase != null) {
        "$flavorsFullNameCamelCase/$buildType/$apkName.apk"
    } else {
        "$buildType/$apkName.apk"
    }
}
