/*
 * SPDX-FileCopyrightText: 2025 Alexey Illarionov and the wasm2class-gradle-plugin project contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package at.released.wasm2class.test.functional

import java.nio.file.Path
import kotlin.io.path.readBytes

public object TestFixtures {
    internal val userDir: String
        get() = System.getProperty("user.dir")

    internal val functionalTestsMaven: Path
        get() = Path.of(userDir, "build/functional-tests-plugin-repository")

    public object CommonFiles {
        public val root: Path
            get() = Path.of(userDir, "testFixtures/files")

        public val clockWasm: FileContent get() = file("testwasm/clock.wasm")
        public val helloworldWasm: FileContent get() = file("testwasm/helloworld.wasm")

        public fun file(dstPath: String): FileContent = FileContent(dstPath, root.resolve(dstPath).readBytes())
    }

    public object Projects {
        public val rootPath: Path
            get() = Path.of(userDir, "testFixtures/projects")

        public val javaApp: SubprojectTemplateId = SubprojectTemplateId("java-app")

        public val javaLibApp: SubprojectTemplateId = SubprojectTemplateId("javalib-app", "java-lib/javalib-app")
        public val javaLibLib: SubprojectTemplateId = SubprojectTemplateId("javalib-lib", "java-lib/javalib-lib")

        public val kotlinApp: SubprojectTemplateId = SubprojectTemplateId("kotlin-app")

        public val androidJavaApp: SubprojectTemplateId = SubprojectTemplateId("android-java-app")

        public val androidKotlinLibFlavorsApp: SubprojectTemplateId =
            SubprojectTemplateId("android-app", "android-kotlin-lib-flavors/android-app")
        public val androidKotlinLibFlavorsLib1: SubprojectTemplateId =
            SubprojectTemplateId("android-lib1", "android-kotlin-lib-flavors/android-lib1")

        public val kotlinMultiplatformApp: SubprojectTemplateId =
            SubprojectTemplateId("kmp-jvm-android-app", "kmp-jvm-android-app")

        public data class SubprojectTemplateId(
            val projectName: String,
            val srcPath: String = projectName,
            val androidNamespace: String? = null,
        )
    }
}
