# Change Log

## [0.4.0] — 2025-06-19

Bump Chicory version to 1.4.1

This is a binary-incompatible change. Many task names, configurations, classes, and paths have been renamed.

The name of the declared module now corresponds to the name of the generated class, i.e., the "Module" suffix is no longer added automatically.

Configuration names changed:
* `chicoryAotCompiler` -> `chicoryCompiler`
* `chicoryAotCompilerRuntimeClasspath` -> `chicoryCompilerRuntimeClasspath`

Task names changed:
* `<target>CompileAotModuleWithJavac` -> `<target>CompileWasmModuleWithJavac`

The main build directory changed from <build>/generated-chicory-aot to `<build>/generated-chicory`

Added the ability to specify a list of function numbers to be executed using the interpreter.

Also check https://chicory.dev/blog/chicory-1.4.0

#### 🤖 Maintenance

- Test with AGP 8.12 alpha, 8.11.RC, 8.10.1 and Kotlin 2.1.21
- Bump AGP to 8.10.1 and other dependencies

## [0.3] — 2025-04-06

#### 🤖 Dependencies

- Bump Chicory version to 1.2.1

#### 🔧 Maintenance

- Bump gradle-maven-publish-plugin to 0.31.0
- Test with AGP 8.9.1 and Kotlin 2.1.20

## [0.2] — 2025-03-07

#### 💅 Polish

- Class generation has been reworked to use the *aot-build-time-experimental* module from Chicory.
- Gradle isolation mode has been switched from classloaderIsolation to processIsolation.
- Chicory runtime dependency is now added to the *implementation* configuration instead of *api*.

## [0.1] — 2025-03-03

* Initial release.
