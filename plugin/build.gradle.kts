/*
 * SPDX-FileCopyrightText: 2025 Alexey Illarionov and the wasm2class-gradle-plugin project contributors
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Suppress("UnstableApiUsage")

import com.vanniktech.maven.publish.GradlePublishPlugin
import com.vanniktech.maven.publish.SonatypeHost
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode.Warning
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `kotlin-dsl-base`
    alias(libs.plugins.gradle.maven.publish.plugin.base)
    alias(libs.plugins.gradle.plugin.publish)
    alias(libs.plugins.kotlinx.binary.compatibility.validator)
}

group = "at.released.wasm2class"
version = "0.5.0-SNAPSHOT"

kotlin {
    explicitApi = Warning

    @Suppress("DEPRECATION")
    compilerOptions {
        jvmTarget = JvmTarget.JVM_11
        // https://docs.gradle.org/current/userguide/compatibility.html#kotlin
        apiVersion = org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_1_6
        languageVersion = org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_1_6
        freeCompilerArgs.addAll("-Xjvm-default=all")
    }
}

tasks.withType<AbstractArchiveTask>().configureEach {
    isPreserveFileTimestamps = false
    isReproducibleFileOrder = true
}

tasks.withType<JavaCompile>().configureEach {
    sourceCompatibility = JavaVersion.VERSION_11.toString()
    targetCompatibility = JavaVersion.VERSION_11.toString()
}

dependencies {
    compileOnly(libs.chicory.build.time.compiler)
    compileOnly(libs.chicory.runtime)
    compileOnly(libs.chicory.wasm)
    compileOnly(libs.agp.plugin.api)
    compileOnly(libs.kotlin.gradle.plugin)
}

val functionalTestRepository = layout.buildDirectory.dir("functional-tests-plugin-repository")
testing {
    suites {
        val test by getting(JvmTestSuite::class) {
            useJUnitJupiter(libs.versions.junit5)
            targets {
                all {
                    testTask.configure {
                        configureTestTaskDefaults()
                    }
                }
            }
            dependencies {
                implementation(project(":functional-test-utils"))
                implementation(platform(libs.junit.bom))

                implementation(libs.assertk)
                implementation(libs.junit.jupiter.api)
                implementation(libs.junit.jupiter.params)
                runtimeOnly(libs.junit.jupiter.engine)
            }
        }

        val functionalTestSuites = setOf("functionalTest", "functionalMatrixTest")

        functionalTestSuites.forEach { register<JvmTestSuite>(it) }

        withType(JvmTestSuite::class).matching { it.name in functionalTestSuites }.configureEach {
            useJUnitJupiter(libs.versions.junit5)

            dependencies {
                implementation(project(":functional-test-utils"))
                implementation(libs.assertk)
            }

            targets {
                all {
                    testTask.configure {
                        configureTestTaskDefaults()
                        dependsOn(tasks.named("publishAllPublicationsToFunctionalTestsRepository"))
                        inputs.dir(functionalTestRepository)
                        shouldRunAfter(test)
                        listOf(
                            "testAgpVersion" to "TEST_AGP_VERSION",
                            "testChicoryVersion" to "TEST_CHICORY_VERSION",
                            "testGradleVersion" to "TEST_GRADLE_VERSION",
                            "testKotlinVersion" to "TEST_KOTLIN_VERSION",
                        ).forEach { (inputProperty, envVariable) ->
                            inputs
                                .property(inputProperty) { System.getenv(envVariable) }
                                .optional(true)
                        }
                    }
                }
            }
        }
    }
}

private fun Test.configureTestTaskDefaults() {
    maxHeapSize = "1512M"
    jvmArgs = listOf("-XX:MaxMetaspaceSize=768M")
    testLogging {
        if (providers.gradleProperty("verboseTest").map(String::toBoolean).getOrElse(false)) {
            events = setOf(TestLogEvent.FAILED, TestLogEvent.STANDARD_ERROR, TestLogEvent.STANDARD_OUT)
        } else {
            events = setOf(TestLogEvent.FAILED)
        }
    }
    environment["TEST_WASM2CLASS_PLUGIN_VERSION"] = version
    environment["TEST_CHICORY_VERSION"] = providers.environmentVariable("TEST_CHICORY_VERSION")
        .orElse(libs.versions.chicory)
        .get()
    javaLauncher = javaToolchains.launcherFor {
        languageVersion = providers.environmentVariable("TEST_JDK_VERSION")
            .map { JavaLanguageVersion.of(it.toInt()) }
            .orElse(JavaLanguageVersion.of(21))
    }
}

gradlePlugin.testSourceSets.add(sourceSets["functionalTest"])

tasks.named<Task>("check") {
    dependsOn(testing.suites.named("functionalTest"))
}

gradlePlugin {
    website = "https://github.com/illarionov/wasm2class-gradle-plugin"
    vcsUrl = "https://github.com/illarionov/wasm2class-gradle-plugin"
    plugins.create("wasm2class") {
        id = "at.released.wasm2class.plugin"
        implementationClass = "at.released.wasm2class.Wasm2ClassPlugin"
        displayName = "Wasm2class Gradle Plugin"
        description = "This plugin compiles WebAssembly (.wasm) files into Java class files (.class) " +
                "with AOT bytecode for Chicory WebAssembly runtime"
        tags = listOf("android", "wasm")
    }
    plugins.create("wasm2classBase") {
        id = "at.released.wasm2class.plugin.base"
        implementationClass = "at.released.wasm2class.Wasm2ClassBasePlugin"
        displayName = "Wasm2class Base Gradle Plugin"
    }
}

mavenPublishing {
    configure(GradlePublishPlugin())
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
    publishing {
        repositories {
            maven {
                name = "functionalTests"
                setUrl(functionalTestRepository)
            }
        }
    }
    signAllPublications()
    extensions.getByType(SigningExtension::class.java).isRequired = false

    pom {
        name.set(project.name)
        description.set(
            "Gradle plugin that allows you to compile .wasm files into .class files with AOT bytecode for " +
                    "Chicory WebAssembly runtime",
        )
        url.set("https://github.com/illarionov/wasm2class-gradle-plugin")

        licenses {
            license {
                name.set("The Apache License, Version 2.0")
                url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                distribution.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }
        developers {
            developer {
                id.set("illarionov")
                name.set("Alexey Illarionov")
                email.set("alexey@0xdc.ru")
            }
        }
        scm {
            connection.set("scm:git:git://github.com/illarionov/wasm2class-gradle-plugin.git")
            developerConnection.set("scm:git:ssh://github.com:illarionov/wasm2class-gradle-plugin.git")
            url.set("https://github.com/illarionov/wasm2class-gradle-plugin/tree/main")
        }
    }
}
