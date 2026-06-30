# Freaking RPG

A Paper plugin for building a full-fledged RPG in Minecraft using plugins and datapacks.

Targets **Minecraft 26.2** on **Paper** with **Java 25**.

## Quick setup (Windows)

Same layout as Breach: the project lives in `Projects\Freaking-RPG` on your PC.

This repo is **private**, so the `irm https://raw.githubusercontent.com/...` one-liner from Breach returns 404. Clone first, then run the setup script:

```powershell
gh repo clone aidencole/Freaking-RPG "$env:USERPROFILE\Projects\Freaking-RPG"
& "$env:USERPROFILE\Projects\Freaking-RPG\scripts\setup-dev.ps1"
```

No `gh`? Use Git instead (you must be logged into GitHub on this PC):

```powershell
git clone https://github.com/aidencole/Freaking-RPG.git "$env:USERPROFILE\Projects\Freaking-RPG"
& "$env:USERPROFILE\Projects\Freaking-RPG\scripts\setup-dev.ps1"
```

The script installs JDK 25 if needed, updates the repo, downloads Gradle dependencies, and prints IntelliJ open steps.

You do **not** need to create an IntelliJ project manually — open the cloned folder and Gradle import handles the rest.

## Requirements

- JDK 25
- IntelliJ IDEA 2026.2 (recommended)
- [Minecraft Development](https://plugins.jetbrains.com/plugin/8327-minecraft-development) IntelliJ plugin (optional but helpful)

## Open in IntelliJ

1. Run the setup script above, or clone this repo to `Projects\Freaking-RPG`.
2. In IntelliJ: **File → Open** → select that folder (the one containing `build.gradle.kts`).
3. Trust the project and wait for Gradle sync.
4. Set the project SDK to **JDK 25** if it is not detected automatically.
5. Use the **runServer** Gradle task to launch a local Paper test server with the plugin loaded.

## Build

```bash
./gradlew build
```

The plugin JAR is written to `build/libs/FreakingRPG-0.1.0-SNAPSHOT.jar`.

## Run locally

```bash
./gradlew runServer
```

First run downloads Paper; later runs reuse the cached server in `run/`.

## Install on a server

1. Download [Paper 26.2](https://papermc.io/downloads/paper) (or 26.1.x for production).
2. Copy the built JAR into the server's `plugins/` folder.
3. Start the server and run `/frpg help`.

## Project layout

```
src/main/java/dev/freakingrpg/
  FreakingRpgPlugin.java    # Plugin entry point
  core/                     # Core systems (commands, services, etc.)
src/main/resources/
  plugin.yml
  config.yml
scripts/
  setup-dev.ps1             # One-shot Windows dev setup
```

## License

All Rights Reserved.
