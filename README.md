## Continuous Digital Twin Evolution Driven by Experimentation

### Prerequisites

1. Install R so that it is accessible from the command line
2. Install the R package "dunn.test" (i.e., `install.packages("dunn.test")`)

#### Generate the .ecore and .genmodel files

Beware that this task requires Java 1.8 and Gradle 5.3 (which has been included in this repository). Otherwise, the Ecore types generated will always be `EJavaObject`.

```
./gradlew xcoreProcess
```

#### Compile the Java code

```
./gradlew compileJava
```

#### Run tests and static analysis

Tests, PMD and checkstyle:

```
./gradlew check
```

Tests only:

```
./gradlew test
```

#### Build the sources

Unlike `compileJava`, `build` will generate a `.zip` file containing the distributions files.

```bash
./gradlew build
```
You can optionally skip the execution of static analysis and tests:

```bash
./gradlew build -x check
```

#### Run the main module

```
./gradlew evolution:run
```

#### Create a new release

```bash
export TAG=0.1.0
git tag -a $TAG -m "$TAG"
git push origin $TAG
```
