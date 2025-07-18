/*
 * SPDX-FileCopyrightText: 2025 Alexey Illarionov and the wasm2class-gradle-plugin project contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package at.released.wasm2class.test.functional.testmatrix

import at.released.wasm2class.test.functional.testmatrix.compatibility.AgpVersionCompatibility.AGP_8_0_2
import at.released.wasm2class.test.functional.testmatrix.compatibility.AgpVersionCompatibility.AGP_8_10_1
import at.released.wasm2class.test.functional.testmatrix.compatibility.AgpVersionCompatibility.AGP_8_11_1
import at.released.wasm2class.test.functional.testmatrix.compatibility.AgpVersionCompatibility.AGP_8_12_0_ALPHA09
import at.released.wasm2class.test.functional.testmatrix.compatibility.AgpVersionCompatibility.AGP_8_5_2
import at.released.wasm2class.test.functional.testmatrix.compatibility.AgpVersionCompatibility.AGP_8_8_2
import at.released.wasm2class.test.functional.testmatrix.compatibility.AgpVersionCompatibility.AGP_8_9_3
import at.released.wasm2class.test.functional.testmatrix.compatibility.AgpVersionCompatibility.agpIsCompatibleWithGradle
import at.released.wasm2class.test.functional.testmatrix.compatibility.AgpVersionCompatibility.getCompatibleAndroidApiLevel
import at.released.wasm2class.test.functional.testmatrix.compatibility.AgpVersionCompatibility.isAgpCompatibleWithRuntime
import at.released.wasm2class.test.functional.testmatrix.compatibility.GradleVersionCompatibility.GRADLE_8_12_1
import at.released.wasm2class.test.functional.testmatrix.compatibility.GradleVersionCompatibility.GRADLE_8_13
import at.released.wasm2class.test.functional.testmatrix.compatibility.GradleVersionCompatibility.GRADLE_8_14_3
import at.released.wasm2class.test.functional.testmatrix.compatibility.GradleVersionCompatibility.GRADLE_8_8
import at.released.wasm2class.test.functional.testmatrix.compatibility.GradleVersionCompatibility.GRADLE_9_0_0_RC2
import at.released.wasm2class.test.functional.testmatrix.compatibility.GradleVersionCompatibility.isGradleCompatibleWithRuntime
import at.released.wasm2class.test.functional.testmatrix.compatibility.KotlinVersionCompatibility.KOTLIN_2_0_21
import at.released.wasm2class.test.functional.testmatrix.compatibility.KotlinVersionCompatibility.KOTLIN_2_1_10
import at.released.wasm2class.test.functional.testmatrix.compatibility.KotlinVersionCompatibility.KOTLIN_2_1_21
import at.released.wasm2class.test.functional.testmatrix.compatibility.KotlinVersionCompatibility.KOTLIN_2_2_0
import at.released.wasm2class.test.functional.testmatrix.compatibility.KotlinVersionCompatibility.isKotlinCompatible
import org.slf4j.Logger
import org.slf4j.LoggerFactory

public class TestMatrix {
    private val logger: Logger = LoggerFactory.getLogger(TestMatrix::class.java)
    private val defaultVersionCatalog: VersionCatalog = VersionCatalog.getDefault()
    private val gradleVersions = listOf(
        GRADLE_8_8,
        GRADLE_8_12_1,
        GRADLE_8_13,
        GRADLE_8_14_3,
        GRADLE_9_0_0_RC2,
    )

    // See https://developer.android.com/studio/releases/gradle-plugin
    private val agpVersions = listOf(
        AGP_8_0_2,
        AGP_8_5_2,
        AGP_8_8_2,
        AGP_8_9_3,
        AGP_8_10_1,
        AGP_8_11_1,
        AGP_8_12_0_ALPHA09,
    )

    private val kotlinVersions = listOf(
        KOTLIN_2_0_21,
        KOTLIN_2_1_10,
        KOTLIN_2_1_21,
        KOTLIN_2_2_0,
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
                    kotlinVersions().forEach { kotlinVersion ->
                        yield(Triple(gradleVersion, agpVersion, kotlinVersion))
                    }
                }
            }
        }.filter { (gradleVersion, agpVersion, kotlinVersion) ->
            agpIsCompatibleWithGradle(agpVersion, gradleVersion) &&
                    isKotlinCompatible(kotlinVersion, agpVersion, gradleVersion)
        }
    }

    // Allow setting a single, fixed Gradle version via environment variables
    private fun gradleVersions(): List<Version> = systemEnvVersions("TEST_GRADLE_VERSION", gradleVersions)

    // Allow setting a single, fixed AGP version via environment variables
    private fun agpVersions(): List<Version> = systemEnvVersions("TEST_AGP_VERSION", agpVersions)

    // Allow setting a single, fixed Kotlin version via environment variables
    private fun kotlinVersions(): List<Version> = systemEnvVersions("TEST_KOTLIN_VERSION", kotlinVersions)

    private fun systemEnvVersions(envName: String, orElse: List<Version>): List<Version> {
        val version = System.getenv(envName)
        return if (version == null) {
            orElse
        } else {
            listOf(Version.parse(version))
        }
    }
}
