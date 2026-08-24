# Developer Setup & Building (Minecraft 1.20.1)

*[[Home]] / Developer Setup & Building*

---

## 🛠️ Environment Prerequisites

To compile and contribute to **Vanilla Outsider: Better Dogs** on **Minecraft 1.20.1**:

* **Java Development Kit**: JDK 17 (or JDK 25 with `release = 17` toolchain)
* **Gradle**: 9.3+ (or included `./gradlew` wrapper)
* **Loom Version**: `fabric-loom 1.10.2`
* **Mappings**: Mojang Official Mappings (`loom.officialMojangMappings()`)

---

## 🚀 Build Commands

Execute the following commands from the subproject root directory (`Better Dogs v1.20.1/Better Dogs 1.20.1`):

### Compile & Build Tagged JAR
```bash
./gradlew build --no-daemon
```

### Run Automated Headless Tests
```bash
./gradlew test --no-daemon
```

---

## 📂 Artifact Output
Compiled release JARs are generated in `build/libs/` as:
* `better-dogs-1.0.70+1.20.1.jar`

And automatically archived to:
* `Archive Jar of all versions/MC 1.20.1/better-dogs-1.0.70+1.20.1.jar`
