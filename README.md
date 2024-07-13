# Usage on unix-like systems

Use java 21 and the following two commands.

```
javac -cp lib/sim4da-v2.jar -d bin $(find src -name "*.java")
java -cp bin:lib/sim4da-v2.jar ueb2.Simulation NUMBER_OF_ACTORS
```
