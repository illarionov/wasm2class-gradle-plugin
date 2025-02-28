/*
 * SPDX-FileCopyrightText: 2025 Alexey Illarionov and the wasm2class-gradle-plugin project contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package at.released.wasm2class.test.functional.assertions

import assertk.Assert
import assertk.assertions.isNotNull
import assertk.assertions.prop
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.BuildTask
import org.gradle.testkit.runner.TaskOutcome

public fun Assert<BuildResult>.outcomeOfTask(taskName: String): Assert<TaskOutcome> = prop(BuildResult::getTasks)
    .outcomeOf(taskName)

public fun Assert<BuildResult>.resultOfTask(taskName: String): Assert<BuildTask> = prop(BuildResult::getTasks)
    .resultOf(taskName)

public fun Assert<List<BuildTask>>.outcomeOf(taskName: String): Assert<TaskOutcome> = resultOf(taskName)
    .prop(BuildTask::getOutcome)

public fun Assert<List<BuildTask>>.resultOf(taskName: String): Assert<BuildTask> = this.transform { tasks ->
    tasks.singleOrNull { it.path == taskName }
}.isNotNull()
