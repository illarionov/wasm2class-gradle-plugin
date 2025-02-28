/*
 * SPDX-FileCopyrightText: 2025 Alexey Illarionov and the wasm2class-gradle-plugin project contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package at.released.wasm2class.test.functional.testmatrix

import at.released.wasm2class.test.functional.testmatrix.compatibility.AgpVersionCompatibility.AGP_8_4_2
import at.released.wasm2class.test.functional.testmatrix.compatibility.AgpVersionCompatibility.AGP_8_8_2
import at.released.wasm2class.test.functional.testmatrix.compatibility.AgpVersionCompatibility.agpIsCompatibleWithGradle
import at.released.wasm2class.test.functional.testmatrix.compatibility.AgpVersionCompatibility.getCompatibleAndroidApiLevel
import at.released.wasm2class.test.functional.testmatrix.compatibility.AgpVersionCompatibility.isAgpCompatibleWithRuntime
import at.released.wasm2class.test.functional.testmatrix.compatibility.GradleVersionCompatibility.GRADLE_7_6_4
import at.released.wasm2class.test.functional.testmatrix.compatibility.GradleVersionCompatibility.GRADLE_8_13
import at.released.wasm2class.test.functional.testmatrix.compatibility.GradleVersionCompatibility.isGradleCompatibleWithRuntime
import at.released.wasm2class.test.functional.testmatrix.compatibility.KotlinVersionCompatibility
import org.slf4j.Logger
import org.slf4j.LoggerFactory

public class TestMatrix {
    private val logger: Logger = LoggerFactory.getLogger(TestMatrix::class.java)
    private val defaultVersionCatalog: VersionCatalog = VersionCatalog.getDefault()
    private val gradleVersions = listOf(
        GRADLE_7_6_4,
        GRADLE_8_13,
    )

    // See https://developer.android.com/studio/releases/gradle-plugin
    private val agpVersions = listOf(
        AGP_8_4_2,
        AGP_8_8_2,
    )

    private val kotlinVersions = listOf(
        KotlinVersionCompatibility.KOTLIN_2_1_10,
        KotlinVersionCompatibility.KOTLIN_2_1_20_RC,
    )

    public fun getMainTestVariants(): List<VersionCatalog> = getCompatibleGradleAgpVariants()
        .map { (gradleVersion, agpVersion, kotlinVersion) ->
            val compileTargetSdk = getCompatibleAndroidApiLevel(agpVersion)
            defaultVersionCatalog.copy(
                gradleVersion = gradleVersion,
                agpVersion = agpVersion,
                compileSdk = compileTargetSdk,
                targetSdk = compileTargetSdk,
                kotlinVersion = kotlinVersion,
            )
        }
        .toList()
        .also { catalogs ->
            require(catalogs.isNotEmpty()) {
                "Found no compatible AGP and Gradle version combination, check your supplied arguments."
            }
        }

    private fun getCompatibleGradleAgpVariants(): Sequence<Triple<Version, Version, Version>> {
        val (gradleCompatibleVersions, gradleIncompatibleVersions) = gradleVersions().partition {
            isGradleCompatibleWithRuntime(it.baseVersion())
        }

        if (gradleIncompatibleVersions.isNotEmpty()) {
            logger.warn(
                "Gradle versions {} cannot be run on the current JVM `{}`",
                gradleIncompatibleVersions.joinToString(),
                Runtime.version(),
            )
        }

        val (agpCompatibleVersions, agpIncompatibleVersions) = agpVersions().partition {
            isAgpCompatibleWithRuntime(it)
        }

        if (agpIncompatibleVersions.isNotEmpty()) {
            logger.warn(
                "Android Gradle Plugin versions {} cannot be run on the current JVM `{}`",
                agpIncompatibleVersions.joinToString(),
                Runtime.version(),
            )
        }

        return sequence {
            gradleCompatibleVersions.forEach { gradleVersion ->
                agpCompatibleVersions.forEach { agpVersion ->
                    kotlinVersions.forEach { kotlinVersion ->
                        yield(Triple(gradleVersion, agpVersion, kotlinVersion))
                    }
                }
            }
        }.filter { (gradleVersion, agpVersion, _) ->
            agpIsCompatibleWithGradle(agpVersion, gradleVersion)
        }
    }

    // Allow setting a single, fixed Gradle version via environment variables
    private fun gradleVersions(): List<Version> {
        val gradleVersion = System.getenv("TEST_GRADLE_VERSION")
        return if (gradleVersion == null) {
            gradleVersions
        } else {
            listOf(Version.parse(gradleVersion))
        }
    }

    // Allow setting a single, fixed AGP version via environment variables
    private fun agpVersions(): List<Version> {
        val agpVersion = System.getenv("TEST_AGP_VERSION")
        return if (agpVersion == null) {
            agpVersions
        } else {
            listOf(Version.parse(agpVersion))
        }
    }
}
