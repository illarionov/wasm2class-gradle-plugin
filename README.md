# Wasm2class Gradle Plugin

An experimental Gradle plugin that allows you to compile `.wasm` files into `.class` files with AOT bytecode for
[Chicory WebAssembly runtime][Chicory], making it possible to run WebAssembly modules on JVM and Android platforms.

The plugin is based on the original [Chicory AOT Maven Plugin] — be sure to review its documentation for further details.

The plugin provides integration with key Gradle plugins, including JVM Plugin, Android Gradle Plugin and 
Kotlin Multiplatform plugin (for JVM and Android targets).

[Chicory]: https://chicory.dev/
[Chicory AOT Maven Plugin]: https://chicory.dev/docs/experimental/aot#pre-compiled-aot

## Requirements

* Gradle `8.0` or newer
* Android Gradle Plugin `8.0.2` or newer (for Android projects)
* Kotlin `2.0.21` or newer (when using Kotlin or Kotlin Multiplatform)

## Installation

The latest release is available on Maven Central. Add the following to your plugins block:

```
plugins { 
    id("at.released.wasm2class.plugin") version "0.1-SNAPSHOT"
}
```

## Usage

Assume you have a `helloworld.wasm` file, compiled from the following C code:

```c
#include <stdio.h>

int main() {
  printf("Hello, World!\n");
  return 0;
}
```

You can compile it using [Emscripten](https://emscripten.org/):

```c
emcc -O3 -mbulk-memory helloworld.c -o helloworld.wasm
```

Define the compiled WASM module in the `wasm2class` block of your Gradle project, along with the target package:

```kotlin
wasm2class {
    targetPackage = "com.example.wasm"
    modules {
        create("helloworld") {
            wasm = file("helloworld.wasm")
        }
    }
}
```

This configuration generates the `com.example.wasm.HelloworldModule` class, which includes factory methods
for initializing a Chicory WebAssembly instance.

The following example demonstrates how to instantiate and execute the compiled module:

```kotlin
import com.dylibso.chicory.runtime.Instance
import com.dylibso.chicory.runtime.Store
import com.dylibso.chicory.wasi.WasiExitException
import com.dylibso.chicory.wasi.WasiOptions
import com.dylibso.chicory.wasi.WasiPreview1

private fun helloworldWasm() {
    val wasiOptions = WasiOptions.builder().withStdout(System.out).withStderr(System.err).build()
    WasiPreview1.builder().withOptions(wasiOptions).build().use { wasi ->
        val store = Store().addFunction(*wasi.toHostFunctions())
        val instance = store.instantiate("helloworld") { importValues ->
            Instance.builder(HelloworldModule.load())
                .withMachineFactory(HelloworldModule::create)
                .withImportValues(importValues)
                .withStart(false)
                .build()
        }
        try {
            instance.export("_start").apply()
        } catch (ex: WasiExitException) {
            if (ex.exitCode() != 0) {
                throw ex
            }
        }
    }
}
```

For more examples, including usage with different Gradle plugins, check out the [test projects](https://github.com/illarionov/wasm2class-gradle-plugin/tree/main/plugin/testFixtures/projects)

### Additional Configuration

The plugin supports per-module customization, including custom target package and custom naming for generated classes.

For Android projects, the plugin supports variant-scoped configurations, allowing selective generation of WebAssembly
modules for specific build types or product flavors. 

Example:

```kotlin
androidComponents {
    onVariants(selector().withName("fullPaid")) { variant ->
        variant.getExtension(Wasm2ClassVariantExtension::class.java)?.apply {
            modules {
                create("paid") {
                    wasm = file("paid.wasm")
                    targetPackage = "com.example.full"
                }
            }
        } ?: error("Wasm2ClassExtension extension not registered")
    }
}
```

###  R8 / ProGuard

If you're using R8 or ProGuard in your project, you may need to add the following rules:

```
-dontwarn com.dylibso.chicory.experimental.hostmodule.annotations.Buffer
-dontwarn com.dylibso.chicory.experimental.hostmodule.annotations.HostModule
-dontwarn com.dylibso.chicory.experimental.hostmodule.annotations.WasmExport
-dontwarn com.google.errorprone.annotations.FormatMethod
-dontwarn java.lang.System$Logger$Level
-dontwarn java.lang.System$Logger

-keepclasseswithmembers,allowoptimization public final class **Module {
    public static com.dylibso.chicory.wasm.WasmModule load();
}
```

The `**Module.load()` method in generated modules relies on `XXXModule.class.getResourceAsStream()` 
with a relative path to load a stripped version of the WASM binary. Because of this, it's important to keep 
the original class name and package structure intact. The final ProGuard rule ensures these classes are 
neither renamed nor relocated.

Alternatively, if you manually load the stripped WASM file content instead of using the `load()` method, 
this rule is not required.

## Development notes

Project has 3 test suites:

- `test`: a suite of basic unit tests.
- `functionalTest`: a set of functional tests running on one fixed configuration of AGP, Gradle, Kotlin Multiplatform
  and Java versions.
- `functionalMatrixTest`: a separate set of functional tests running on different combinations of AGP, Gradle,
  Kotlin Multiplatform and Java versions. Not executed with the `test` Gradle lifecycle task.

The source code of the plugin is located in the `plugin` module.

Basic commands:

- Build the plugin: `./gradlew assemble`
- Run unit tests and basic functional tests: `./gradlew test`
- Running matrix tests: `./gradlew functionalMatrixTest`

By default, all tests in the `functionalMatrixTest` suite will be executed against all compatible versions of the
Android Gradle Plugin and Gradle. You can restrict execution to specific versions by setting the following environment
variables:

- `TEST_GRADLE_VERSION`
- `TEST_AGP_VERSION`
- `TEST_JDK_VERSION`
- `TEST_KOTLIN_VERSION`

Example: `TEST_GRADLE_VERSION=8.13 TEST_AGP_VERSION=8.2.0 TEST_JDK_VERSION=17 ./gradlew functionalMatrixTest`

## Contributing

Any type of contributions are welcome. Please see [the contribution guide](CONTRIBUTING.md).

### License

Wasm2class Gradle Plugin is distributed under the terms of the Apache License (Version 2.0). See the
[license](https://github.com/illarionov/wasm2class-gradle-plugin/blob/main/LICENSE) for more information.
