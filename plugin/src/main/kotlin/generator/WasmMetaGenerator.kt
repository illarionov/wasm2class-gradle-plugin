/*
 * SPDX-FileCopyrightText: 2025 Alexey Illarionov and the wasm2class-gradle-plugin project contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package at.released.wasm2class.generator

import com.dylibso.chicory.wasm.Parser
import com.dylibso.chicory.wasm.WasmModule
import com.dylibso.chicory.wasm.WasmWriter
import com.dylibso.chicory.wasm.WasmWriter.writeVarUInt32
import com.dylibso.chicory.wasm.types.OpCode.END
import com.dylibso.chicory.wasm.types.OpCode.UNREACHABLE
import com.dylibso.chicory.wasm.types.RawSection
import com.dylibso.chicory.wasm.types.Section
import com.dylibso.chicory.wasm.types.SectionId.CODE
import com.dylibso.chicory.wasm.types.SectionId.CUSTOM
import java.io.ByteArrayOutputStream

// Based on the https://github.com/dylibso/chicory/tree/main/aot-maven-plugin
// Original license: Apache-2.0

@Suppress("MagicNumber")
internal fun generateWasmMeta(wasmBytes: ByteArray, module: WasmModule): ByteArray {
    val writer = WasmWriter()
    Parser.parseWithoutDecoding(wasmBytes) { section: Section ->
        when (section.sectionId()) {
            CODE -> {
                val newCode = ByteArrayOutputStream().use { out ->
                    val count = module.codeSection().functionBodyCount()
                    writeVarUInt32(out, count)
                    repeat(count) {
                        writeVarUInt32(out, 3) // function size in bytes
                        writeVarUInt32(out, 0) // locals count
                        out.write(UNREACHABLE.opcode())
                        out.write(END.opcode())
                    }
                    out.toByteArray()
                }
                writer.writeSection(CODE, newCode)
            }

            CUSTOM -> Unit
            else -> writer.writeSection(section as RawSection)
        }
    }
    return writer.bytes()
}
