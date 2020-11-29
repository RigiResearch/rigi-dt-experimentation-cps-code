## Continuous Digital Twin Evolution Driven by Experimentation

### Compile the Java code

```
./gradlew compileJava
```

### Build the sources

Unlike `compileJava`, `build` will generate a `.zip` file containing the distributions files.

```bash
./gradlew build
```
You can optionally skip the execution of static analysis and tests:

```bash
./gradlew build -x check
```

### Run the main module

```
./gradlew evolution:run
```

### Create a new release

```bash
export TAG=0.1.0
git tag -a $TAG -m "$TAG"
git push origin $TAG
```
