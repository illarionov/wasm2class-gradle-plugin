/*
 * SPDX-FileCopyrightText: 2025 Alexey Illarionov and the wasm2class-gradle-plugin project contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package at.released.wasm2class.generator

import com.dylibso.chicory.runtime.Instance
import com.dylibso.chicory.runtime.Machine
import com.dylibso.chicory.wasm.Parser
import com.dylibso.chicory.wasm.WasmModule
import com.github.javaparser.StaticJavaParser.parseClassOrInterfaceType
import com.github.javaparser.StaticJavaParser.parseType
import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.Modifier.Keyword.FINAL
import com.github.javaparser.ast.Modifier.Keyword.PRIVATE
import com.github.javaparser.ast.Modifier.Keyword.PUBLIC
import com.github.javaparser.ast.Modifier.Keyword.STATIC
import com.github.javaparser.ast.NodeList
import com.github.javaparser.ast.NodeList.nodeList
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.github.javaparser.ast.body.Parameter
import com.github.javaparser.ast.body.VariableDeclarator
import com.github.javaparser.ast.expr.ClassExpr
import com.github.javaparser.ast.expr.MethodCallExpr
import com.github.javaparser.ast.expr.NameExpr
import com.github.javaparser.ast.expr.ObjectCreationExpr
import com.github.javaparser.ast.expr.StringLiteralExpr
import com.github.javaparser.ast.expr.VariableDeclarationExpr
import com.github.javaparser.ast.stmt.BlockStmt
import com.github.javaparser.ast.stmt.CatchClause
import com.github.javaparser.ast.stmt.ReturnStmt
import com.github.javaparser.ast.stmt.ThrowStmt
import com.github.javaparser.ast.stmt.TryStmt
import com.github.javaparser.utils.SourceRoot
import java.io.IOException
import java.io.InputStream
import java.io.UncheckedIOException
import java.nio.file.Path

internal class WasmModuleClassGenerator(
    private val targetPackage: String,
    private val moduleClassSimpleName: String,
    private val machineClassSimpleName: String,
    private val wasmMetaSimpleName: String,
) {
    fun writeTo(destination: Path) {
        val compilationUnit = generate()
        val dest = SourceRoot(destination)
        dest.add(targetPackage, "$moduleClassSimpleName.java", compilationUnit)
        dest.saveAll()
    }

    private fun generate(): CompilationUnit {
        return CompilationUnit(targetPackage).apply {
            addClass(moduleClassSimpleName, PUBLIC, FINAL).also { type: ClassOrInterfaceDeclaration ->
                type.addConstructor(PRIVATE).createBody()
                generateCreateMethod(type)
                generateLoadMethod(type)
            }
        }
    }

    private fun CompilationUnit.generateCreateMethod(
        type: ClassOrInterfaceDeclaration,
    ) {
        addImport(Instance::class.java)
        addImport(Machine::class.java)

        val method = type.addMethod("create", PUBLIC, STATIC)
            .addParameter(parseType("Instance"), "instance")
            .setType(Machine::class.java)
            .createBody()

        val constructorInvocation = ObjectCreationExpr(
            null,
            parseClassOrInterfaceType(machineClassSimpleName),
            nodeList(NameExpr("instance")),
        )
        method.addStatement(ReturnStmt(constructorInvocation))
    }

    private fun CompilationUnit.generateLoadMethod(
        type: ClassOrInterfaceDeclaration,
    ) {
        addImport(IOException::class.java)
        addImport(UncheckedIOException::class.java)
        addImport(Parser::class.java)
        addImport(WasmModule::class.java)
        addImport(InputStream::class.java)

        val getResource = MethodCallExpr(
            ClassExpr(parseType(moduleClassSimpleName)),
            "getResourceAsStream",
            NodeList(StringLiteralExpr(wasmMetaSimpleName)),
        )
        val resourceVar = VariableDeclarationExpr(
            VariableDeclarator(parseType("InputStream"), "in", getResource),
        )

        val returnStmt = ReturnStmt(
            MethodCallExpr().setScope(NameExpr("Parser")).setName("parse").addArgument(NameExpr("in")),
        )

        val newException: ObjectCreationExpr =
            ObjectCreationExpr()
                .setType(parseClassOrInterfaceType("UncheckedIOException"))
                .addArgument(StringLiteralExpr("Failed to load AOT WASM module"))
                .addArgument(NameExpr("e"))
        val catchIoException = CatchClause()
            .setParameter(Parameter(parseClassOrInterfaceType("IOException"), "e"))
            .setBody(BlockStmt(NodeList(ThrowStmt(newException))))

        val loadMethod = type.addMethod("load", PUBLIC, STATIC).setType(WasmModule::class.java)
        loadMethod.createBody().apply {
            addStatement(
                TryStmt()
                    .setResources(NodeList(resourceVar))
                    .setTryBlock(BlockStmt(NodeList(returnStmt)))
                    .setCatchClauses(NodeList(catchIoException)),
            )
        }
    }
}
