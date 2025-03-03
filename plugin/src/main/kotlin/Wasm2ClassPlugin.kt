/*
 * SPDX-FileCopyrightText: 2025 Alexey Illarionov and the wasm2class-gradle-plugin project contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package at.released.wasm2class

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin

public class Wasm2ClassPlugin : Plugin<Project> {
    override fun apply(target: Project): Unit = with(target) {
        plugins.apply(Wasm2ClassBasePlugin::class.java)

        plugins.withType(JavaPlugin::class.java) {
            setupJavaPluginIntegration()
        }

        plugins.withId("com.android.base") {
            setupAndroidPluginIntegration()
        }

        plugins.withId("org.jetbrains.kotlin.multiplatform") {
            setupKotlinMultiplatformPluginIntegration()
        }
    }
}
