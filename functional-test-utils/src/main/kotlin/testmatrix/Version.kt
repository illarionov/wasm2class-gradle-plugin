/*
 * SPDX-FileCopyrightText: 2025 Alexey Illarionov and the wasm2class-gradle-plugin project contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package at.released.wasm2class.test.functional.testmatrix

import java.util.Objects
import kotlin.IllegalArgumentException

/**
 * Lightweight port of [org.gradle.util.internal.VersionNumber]
 */
public class Version(
    private val major: Int,
    private val minor: Int,
    private val patch: Int? = null,
    private val qualifier: String? = null,
) : Comparable<Version> {
    public fun baseVersion(): Version = Version(major, minor, patch, null)

    override fun compareTo(other: Version): Int = when {
        major != other.major -> major - other.major
        minor != other.minor -> minor - other.minor
        patch != other.patch -> (patch ?: 0) - (other.patch ?: 0)
        else -> -(qualifier ?: "").compareTo(other.qualifier ?: "")
    }

    override fun equals(other: Any?): Boolean = other is Version && compareTo(other) == 0
    override fun hashCode(): Int = Objects.hash(major, minor, patch, qualifier)

    override fun toString(): String = when {
        qualifier != null -> if (patch != null) {
            "$major.$minor.$patch-$qualifier"
        } else {
            "$major.$minor-$qualifier"
        }

        patch != null -> "$major.$minor.$patch"
        else -> "$major.$minor"
    }

    public companion object {
        @Suppress("MagicNumber")
        public fun parse(version: String): Version = try {
            val versionSplits = version.split('.')
            check(versionSplits.size == 2 || versionSplits.size == 3)

            val lastPartSplits = versionSplits.last().split('-', limit = 2)
            check(lastPartSplits.size in 1..2)

            val major = versionSplits[0].toInt()
            val minor: Int
            val patch: Int?
            if (versionSplits.size == 2) {
                minor = lastPartSplits[0].toInt()
                patch = null
            } else {
                minor = versionSplits[1].toInt()
                patch = lastPartSplits[0].toInt()
            }
            Version(
                major = major,
                minor = minor,
                patch = patch,
                qualifier = lastPartSplits.getOrNull(1),
            )
        } catch (@Suppress("TooGenericExceptionCaught") exception: Exception) {
            throw IllegalArgumentException("Could not parse version $version", exception)
        }
    }
}
