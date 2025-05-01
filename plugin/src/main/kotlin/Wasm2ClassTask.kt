/*
 * SPDX-FileCopyrightText: 2025 Alexey Illarionov and the wasm2class-gradle-plugin project contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package at.released.wasm2class

import at.released.wasm2class.InterpreterFallback.FAIL
import at.released.wasm2class.InterpreterFallback.SILENT
import at.released.wasm2class.InterpreterFallback.WARN
import at.released.wasm2class.Wasm2ClassConstants.Configurations.CHICORY_COMPILER_RUNTIME_CLASSPATH
import at.released.wasm2class.Wasm2ClassTask.GenerateChicoryMachineClasses.Companion.createEmpty
import at.released.wasm2class.Wasm2ClassTask.GenerateChicoryMachineClasses.WasmBuildTimeWorkParameters
import com.dylibso.chicory.build.time.compiler.Config
import com.dylibso.chicory.build.time.compiler.Generator
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Classpath
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskProvider
import org.gradle.workers.WorkAction
import org.gradle.workers.WorkParameters
import org.gradle.workers.WorkQueue
import org.gradle.workers.WorkerExecutor
import java.io.IOException
import java.nio.file.FileVisitResult
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.SimpleFileVisitor
import java.nio.file.attribute.BasicFileAttributes
import javax.inject.Inject
import kotlin.io.path.createDirectories
import com.dylibso.chicory.compiler.InterpreterFallback as ChicoryInterpreterFallback

@CacheableTask
public abstract class Wasm2ClassTask @Inject constructor(
    private val workerExecutor: WorkerExecutor,
) : DefaultTask() {
    /**
     * The WASM binaries to be compiled.
     */
    @get:Nested
    public abstract val modules: ListProperty<Wasm2ClassMachineModuleSpec>

    /**
     * The output directory for the generated Java .class files.
     */
    @get:OutputDirectory
    public abstract val outputClasses: DirectoryProperty

    /**
     *  The output directory for the generated Java source files.
     */
    @get:OutputDirectory
    public abstract val outputSources: DirectoryProperty

    /**
     *  The output directory for the generated resources.
     */
    @get:OutputDirectory
    public abstract val outputResources: DirectoryProperty

    /**
     * The classpath with Chicory Build-time compiler.
     */
    @get:InputFiles
    @get:Classpath
    public abstract val chicoryClasspath: ConfigurableFileCollection

    @TaskAction
    public fun execute() {
        val modules = this.modules.get()
        if (modules.isEmpty()) {
            return
        }

        outputSources.asFile.get().toPath().createEmpty()
        outputClasses.asFile.get().toPath().createEmpty()
        outputResources.asFile.get().toPath().createEmpty()

        val workQueue: WorkQueue = workerExecutor.processIsolation {
            classpath.from(chicoryClasspath)
        }
        modules.forEach { binarySpec: Wasm2ClassMachineModuleSpec ->
            workQueue.submit(GenerateChicoryMachineClasses::class.java) {
                wasm.set(binarySpec.wasm)
                outputClasses.set(this@Wasm2ClassTask.outputClasses)
                outputSources.set(this@Wasm2ClassTask.outputSources)
                outputResources.set(this@Wasm2ClassTask.outputResources)
                outputClassPrefix.set(binarySpec.outputClassPrefix)
                interpreterFallback.set(binarySpec.interpreterFallback)
                interpretedFunctions.set(binarySpec.interpretedFunctions)
            }
        }
    }

    internal abstract class GenerateChicoryMachineClasses @Inject constructor() :
        WorkAction<WasmBuildTimeWorkParameters> {
        override fun execute() {
            val config = Config.builder().apply {
                withWasmFile(parameters.wasm.asFile.get().toPath())
                withName(parameters.outputClassPrefix.get())
                withTargetClassFolder(parameters.outputClasses.asFile.get().toPath())
                withTargetSourceFolder(parameters.outputSources.asFile.get().toPath())
                withTargetWasmFolder(parameters.outputResources.asFile.get().toPath())
                withInterpreterFallback(parameters.interpreterFallback.get().chicory)
                withInterpretedFunctions(parameters.interpretedFunctions.getOrElse(emptySet()))
            }.build()
            Generator(config).run {
                generateSources()
                val finalInterpretedFunctions = generateResources()
                generateMetaWasm(finalInterpretedFunctions)
            }
        }

        internal interface WasmBuildTimeWorkParameters : WorkParameters {
            val wasm: RegularFileProperty
            val outputClasses: DirectoryProperty
            val outputSources: DirectoryProperty
            val outputResources: DirectoryProperty
            val outputClassPrefix: Property<String>
            val interpreterFallback: Property<InterpreterFallback>
            val interpretedFunctions: SetProperty<Int>
        }

        internal companion object {
            internal fun Project.registerWasm2ClassTask(
                name: String = "precompileWasm2Class",
                wasm2ClassExtension: Wasm2ClassExtension = extensions.getByType(Wasm2ClassExtension::class.java),
            ): TaskProvider<Wasm2ClassTask> = registerWasm2ClassTaskBase(name) {
                modules.set(wasm2ClassExtension.modules)
                outputClasses.set(wasm2ClassExtension.outputClasses)
                outputSources.set(wasm2ClassExtension.outputSources)
                outputResources.set(wasm2ClassExtension.outputResources)
            }

            internal inline fun Project.registerWasm2ClassTaskBase(
                name: String,
                crossinline block: Wasm2ClassTask.() -> Unit = {},
            ): TaskProvider<Wasm2ClassTask> = tasks.register(name, Wasm2ClassTask::class.java) {
                this.chicoryClasspath.from(configurations.named(CHICORY_COMPILER_RUNTIME_CLASSPATH))
                block()
            }

            internal fun Path.createEmpty(): Path {
                val fullPath = createDirectories()
                Files.walkFileTree(
                    fullPath,
                    object : SimpleFileVisitor<Path>() {
                        override fun visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult {
                            Files.delete(file)
                            return FileVisitResult.CONTINUE
                        }

                        override fun postVisitDirectory(dir: Path, exc: IOException?): FileVisitResult {
                            if (this@createEmpty != dir) {
                                Files.delete(dir)
                                return FileVisitResult.CONTINUE
                            } else {
                                return FileVisitResult.TERMINATE
                            }
                        }
                    },
                )
                return fullPath
            }

            private val InterpreterFallback.chicory: ChicoryInterpreterFallback
                get() = when (this) {
                    SILENT -> ChicoryInterpreterFallback.SILENT
                    WARN -> ChicoryInterpreterFallback.WARN
                    FAIL -> ChicoryInterpreterFallback.FAIL
                }
        }
    }
}
