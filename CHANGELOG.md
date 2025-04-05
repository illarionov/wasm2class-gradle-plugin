# Change Log

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
