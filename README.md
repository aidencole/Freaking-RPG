# Freaking RPG

A Paper plugin for building a full-fledged RPG in Minecraft using plugins and datapacks.

Targets **Minecraft 26.2** on **Paper** with **Java 25**.

## Quick setup (Windows)

Same layout as Breach: the project lives in `Projects\Freaking-RPG` on your PC.

This repo is **private**, so the `irm https://raw.githubusercontent.com/...` one-liner from Breach returns 404. Clone first, then run the setup script:

```powershell
$project = "$env:USERPROFILE\Projects\Freaking-RPG"
if (-not (Test-Path "$project\.git")) {
    gh repo clone aidencole/Freaking-RPG $project
}
& "$project\scripts\setup-dev.ps1"
```

No `gh`? Use Git instead (you must be logged into GitHub on this PC):

```powershell
$project = "$env:USERPROFILE\Projects\Freaking-RPG"
if (-not (Test-Path "$project\.git")) {
    git clone https://github.com/aidencole/Freaking-RPG.git $project
}
& "$project\scripts\setup-dev.ps1"
```

Already cloned? Just run the setup script from the repo:

```powershell
cd $env:USERPROFILE\Projects\Freaking-RPG
git pull origin main
.\scripts\setup-dev.ps1
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

## Test your first boss

The best place to start is the **boss combat framework** — telegraphed attacks, semi-predictable patterns, phase shifts, and punish windows during recovery (Mario / dragon-boss style).

1. Start the dev server: `./gradlew clean build runServer` (accept EULA in `run/eula.txt` if needed).
2. Join the server, make yourself op: `/op YourName`
3. Spawn the demo boss at your feet:

```
/frpg boss spawn colossus_drill
```

4. **Watch the boss bar title** — it tells you which attack is telegraphing.
5. **Dodge the ground tells** — red rings, flame lanes, spirals.
6. **Punish during recovery** — boss takes bonus damage after each attack finishes.
7. At **60% HP** the boss enters an enraged phase with a faster, nastier pattern.
8. Clean up with `/frpg boss stop`.

### The Astronomer (work in progress)

Celestial guardian encounter — arena-as-boss with rotating rings and gravity shifts.

```
/frpg boss spawn astronomer
```

After `git pull`, always **`.\gradlew.bat clean build runServer`** so `run/plugins` gets the latest JAR. On enable the console should log `astronomer arena + HP clamp`. If you see `Health value (2500.0)`, the old plugin is still loaded — delete `run\plugins\FreakingRPG-0.1.0-SNAPSHOT.jar` and rebuild.

This **teleports you** to the void world `frpg_observatory` and builds a circular three-ring platform (quartz / deepslate / polished deepslate) with telescope props. You do **not** need a flat overworld spot — spawning *is* entering the arena.

If spawn fails, check the server console; HP is capped at **1024** (Minecraft limit).

| Phase | HP | What changes |
|-------|-----|----------------|
| Equilibrium | 100–80% | Greatsword, shockwave, constellation lasers |
| Gravity Rotation | 80–60% | Sideways/inverted gravity telegraphs; solar beam + orbital pull |
| Fractured Floor | 60–35% | Three rings spin independently; eclipse + planetfall |
| Celestial Orrery | 35–12% | Sky constellation attacks; telescope arena lights up |
| Unbound Cosmos | 12–0% | Random gravity every 15s; all attacks |

**Core systems:** `RingRotationEngine` (3 concentric rings), `GravityField` (Inception-style pulls), `ObservatoryArena` (telescope + gears), 8 bespoke attacks.

Dev arena is **36 blocks** radius (production target ~120 blocks / 400 ft). ModelEngine body still TBD — currently an Illusioner placeholder at 2.2× scale.

### How the framework works

```
src/main/java/dev/freakingrpg/boss/       # Fight choreography (patterns, phases, telegraphs)
src/main/java/dev/freakingrpg/vfx/        # BlockDisplay debris, shockwaves, ground cracks
src/main/java/dev/freakingrpg/presentation/  # ModelEngine hooks for models + emotes (when installed)
```

**AAA stack (target):** Freaking RPG = brain · ModelEngine = boss/player body · BlockDisplay = world destruction/VFX · resource pack = item visuals.

Shockwave and leap slam now spawn **rock BlockDisplays** that arc outward (not just particles). Install **ModelEngine** on the dev server when you have Blockbench models ready.

## Project layout

```
src/main/java/dev/freakingrpg/
  FreakingRpgPlugin.java    # Plugin entry point
  core/                     # Commands
  boss/                     # Boss combat framework
src/main/resources/
  plugin.yml
  config.yml
scripts/
  setup-dev.ps1             # One-shot Windows dev setup
```

## License

All Rights Reserved.
