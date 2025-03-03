/*
 * Copyright (c) 2023, the fbase-config-generator-gradle-plugin project authors and contributors.
 * Please see the AUTHORS file for details.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package at.released.wasm2class.test.functional.apk

import com.android.tools.apk.analyzer.ArchiveContext
import com.android.tools.apk.analyzer.Archives
import com.android.tools.apk.analyzer.dex.DexDisassembler
import com.android.tools.apk.analyzer.dex.DexFiles
import com.android.tools.apk.analyzer.dex.PackageTreeCreator
import com.android.tools.apk.analyzer.internal.SigUtils
import java.nio.file.Path
import kotlin.LazyThreadSafetyMode.NONE
import kotlin.io.path.useDirectoryEntries

public class ApkInspector private constructor(
    public val path: Path,
    private val archiveContext: ArchiveContext,
) : AutoCloseable {
    public val contextRoot: Path get() = archiveContext.archive.contentRoot

    private val dexes: Map<Path, DexDisassembler> by lazy(NONE) {
        archiveContext.archive.contentRoot.useDirectoryEntries("*.dex") { dexPaths: Sequence<Path> ->
            dexPaths
                .map { it to DexDisassembler(DexFiles.getDexFile(it), null) }
                .toMap()
        }
    }

    public fun getDexCode(
        classFqcn: String = "com.example.wasm2class.config.FirebaseOptionsKt",
        methodSignature: String? = "<clinit>()V",
    ): String? {
        return dexes.firstNotNullOfOrNull { (_, disassembler) ->
            getDexCodeOrNull(disassembler, classFqcn, methodSignature)
        }
    }

    @Suppress("SwallowedException")
    private fun getDexCodeOrNull(
        disassembler: DexDisassembler,
        classFqcn: String,
        methodSignature: String?,
    ): String? {
        if (methodSignature == null) {
            try {
                return disassembler.disassembleClass(classFqcn)
            } catch (e: IllegalStateException) {
                // this dex file doesn't contain the given class.
                // continue searching
            }
        } else {
            try {
                val originalFqcn = PackageTreeCreator.decodeClassName(SigUtils.typeToSignature(classFqcn), null)
                return disassembler.disassembleMethod(
                    classFqcn,
                    SigUtils.typeToSignature(originalFqcn) + "->" + methodSignature,
                )
            } catch (e: IllegalStateException) {
                // this dex file doesn't contain the given method.
                // continue searching
            }
        }
        return null
    }

    override fun close() {
        archiveContext.close()
    }

    public companion object {
        public operator fun invoke(path: Path): ApkInspector = ApkInspector(path, Archives.open(path))
    }
}
