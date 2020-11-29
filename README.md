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
