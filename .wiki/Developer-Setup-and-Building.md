# Developer Setup & Building Guide

*[[Home]] / Developer Setup & Building*

---

## 🛠️ Environment Prerequisites

To compile **Vanilla Outsider: Better Dogs** from source, ensure your development workspace meets the following modern toolchain requirements:

* **Java Development Kit (JDK)**: JDK 25 (64-bit) configured in `JAVA_HOME` or `gradle.properties` (`org.gradle.java.home`).
* **Build System**: Gradle 9.3+ wrapper (`./gradlew`).
* **Plugin Architecture**: Fabric Loom 1.15+.
* **IDE Recommendation**: IntelliJ IDEA 2025.1+ or Eclipse with Java 25 support.

---

## 📥 Cloning & Repository Setup

Clone the repository and select your target Minecraft version directory:

```bash
# Clone the repository
git clone https://github.com/Rifaditya/Vanilla-Outsider-Better-Dogs.git
cd Vanilla-Outsider-Better-Dogs

# Target Minecraft 26.2 directory structure
cd "Better Dogs v26.2/Better Dogs 26.2"
```

---

## 🏗️ Building from Source

Execute the Gradle build command using the `--no-daemon` flag:

```bash
# On Linux / macOS
./gradlew build --no-daemon

# On Windows PowerShell
.\gradlew.bat build --no-daemon
```

### Output Build Artifacts
Upon clean compilation and test execution:
1. The built release JAR will be generated in `build/libs/`:
   `vanilla-outsider-better-dogs-4.24.1+26.2.jar`
2. **Archiving Requirement**: Following the project release lifecycle, the tagged JAR is copied into the outer parent archive directory:
   `e:\Minecraft Project\Vanilla Outsider Collections\Better Dogs\Archive Jar of all versions\`

---

## 🧪 Running Headless Automated Tests

**Better Dogs** integrates JUnit and Fabric Loom GameTest automated test suites (`gradle-tester` engine) to verify entity math, genetics calculations, and AI logic prior to release:

```bash
# Run automated headless test suite
./gradlew test --no-daemon
```

---

## 💻 IDE Import Instructions (IntelliJ IDEA)

1. Open IntelliJ IDEA and select **Open**.
2. Navigate to the project subproject directory (e.g. `Better Dogs v26.2/Better Dogs 26.2`).
3. Select `build.gradle` and open as a project.
4. Run `./gradlew genSources` to generate Loom decompiled Minecraft source mappings.

---

*Back to [[Home]]*
