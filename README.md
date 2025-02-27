# Wasm2class Gradle Plugin

Gradle plugin based on the the [Chicory AOT Maven Plugin](https://github.com/dylibso/chicory/tree/main/aot-maven-plugin)

## Requirements

The latest version of this plugin requires Gradle `7.5.1` or above

## Installation

Add the plugin in the Application module of your project, where Firebase is initialized:

```
plugins { 
    id("at.released.wasm2class.plugin") version "0.1-SNAPSHOT"
}
```

## Usage

## Development notes

Project has 3 test suites:

- `test`: a suite of basic unit tests.
- `functionalTest`: a set of functional tests running on one fixed configuration of AGP, Gradle, Kotlin Multiplatform
  and Java versions.
- `functionalMatrixTest`: a separate set of functional tests running on different combinations of AGP, Gradle,
  Kotlin Multiplatform and Java versions. Not executed with the `test` Gradle lifecycle task.

The source code of the plugin is located in the `plugin` module.
The `samples` directory contains a project with some sample applications. This project is also build on CI as part of
the test workflow.

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
