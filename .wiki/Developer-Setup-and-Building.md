# Developer Setup & Building (Minecraft 1.21.11 / Winter Drop)

*[[Home]] / Developer Setup & Building*

---

## 🛠️ Environment Prerequisites

To compile and contribute to **Vanilla Outsider: Better Dogs** on **Minecraft 1.21.11**:

* **Java Development Kit**: JDK 21 (or JDK 25 with `release = 21` toolchain)
* **Gradle**: 9.3+ (or included `./gradlew` wrapper)
* **Loom Version**: `net.fabricmc.fabric-loom-remap 1.15-SNAPSHOT`
* **Mappings**: Mojang Official Mappings (`loom.officialMojangMappings()`)

---

## 🚀 Build Commands

Execute the following commands from the subproject root directory (`Better Dogs v1.21.11/Better Dogs 1.21.11`):

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
* `better-dogs-1.0.73+1.21.11.jar`

And automatically archived to:
* `Archive Jar of all versions/MC 1.21.11/better-dogs-1.0.73+1.21.11.jar`
