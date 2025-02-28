/*
 * SPDX-FileCopyrightText: 2025 Alexey Illarionov and the wasm2class-gradle-plugin project contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package at.released.wasm2class.test.functional

public data class FileContent(
    val dstPath: String,
    val content: ByteArray,
) {
    public constructor(dstPath: String, content: String) : this(
        dstPath,
        content.encodeToByteArray(throwOnInvalidSequence = true),
    )

    public val contentString: String
        get() = content.decodeToString(throwOnInvalidSequence = true)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FileContent

        if (dstPath != other.dstPath) return false
        if (!content.contentEquals(other.content)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = dstPath.hashCode()
        result = 31 * result + content.contentHashCode()
        return result
    }
}
