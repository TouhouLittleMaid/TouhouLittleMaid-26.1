# AGENTS.md

## Agent behavior

- Default to Simplified Chinese for user-facing replies; keep code, commands, identifiers, logs, and API names verbatim.

## Project shape

- This is a single NeoForge ModDev Gradle project for `touhou_little_maid`, not an Architectury/multi-loader repo.
- Current project coordinates come from `gradle.properties`: Minecraft `26.1.2`, NeoForge `26.1.2.68-beta`, group `com.github.tartaricacid`.
- Root mod sources live in `src/main/java/com/github/tartaricacid/touhoulittlemaid`; `modules/maid-manager-codegen` is
  the only included subproject.
- Main entrypoints are `TouhouLittleMaid.java` (`@Mod`) and `TouhouLittleMaidClient.java`
  (`@Mod(..., dist = Dist.CLIENT)`).
- Registry wiring is centralized in `TouhouLittleMaid.initRegister(...)` through `init/Init*` DeferredRegisters;
  client-only registration is under `client/` and client `@EventBusSubscriber` classes.
- Network payloads are registered in `network/NetworkHandler.register(RegisterPayloadHandlersEvent)` via each packet's
  `TYPE`, `STREAM_CODEC`, and `handle`; prefer existing `sendToClientPlayer`/`sendToNearby` helpers for sends.
- `src/main/resources/META-INF/neoforge.mods.toml` wires the mixin config and access transformer; keep it in sync with
  `touhou_little_maid.mixins.json` and `META-INF/accesstransformer.cfg`.

## Source map

- `init/` holds DeferredRegister declarations; `init/registry/` holds event-driven bootstrap such as common setup,
  commands, compat, datapack reload/listener wiring, and spawn/datapack sync hooks.
- `entity/`, `item/`, `block/`, `blockentity/`, `inventory/`, `crafting/`, `loot/`, `advancements/`, and `world/` are
  the main gameplay/content areas.
- `client/` is client-only code: renderers, GUI/screens, model/reload handling, key input, overlays, and client events.
- `network/` contains payload registration and packet implementations; `network/message/ai/` contains AI-related
  packets.
- `ai/` is the LLM/chat-agent feature area; `compat/` is runtime soft integration with other mods.
- `datagen/` contains data providers used by `runData`; `datapack/` contains runtime datapack reload/data logic.
- `mixin/` contains common mixins, `mixin/client/` client mixins, and `mixin/accessor/` accessors.
- `src/main/resources/` is handwritten assets/data/config wiring; `src/generated/resources/` is datagen output.

## Commands

- Use the wrapper from the repo root. On Windows: `.\gradlew.bat <task>`; on Unix-like shells: `./gradlew <task>`.
- Full verification/build: `.\gradlew.bat build` or `.\gradlew.bat check`.
- Tests: `.\gradlew.bat test`; no committed Java tests were found, and `src/test` is ignored.
- Package the distributable jar with `.\gradlew.bat assemble` or `.\gradlew.bat shadowJar`; the normal `jar` task is
  disabled and `assemble` depends on `shadowJar`.
- NeoForge runs: `.\gradlew.bat runClient`, `runServer`, `runGameTestServer`, and `runData`.
- Focused codegen module check: `.\gradlew.bat :maid-manager-codegen:build`.
- Local publish target is Maven local only: `.\gradlew.bat publishToMavenLocal`.

## Build and generated-source gotchas

- The root project requests Java 25; `:maid-manager-codegen` requests Java 21. `settings.gradle` enables Foojay
  toolchain resolution, so prefer Gradle toolchains over hardcoding `JAVA_HOME`.
- `runData` writes generated resources to `src/generated/resources/`, which is included in the main resources source
  set; avoid hand-editing datagen output unless that is explicitly intended.
- Annotation processing writes Java sources under `build/generated/sources/annotationProcessor/java/main`, also included
  in `sourceSets.main`.
- `MaidManagerDefGenerator` generates `MaidManagers`, `MaidManagerBootstrap`, and `MaidManagerHost` from
  `@MaidManagerDef`; if those classes appear missing, run `compileJava`/`build` instead of creating them manually.
- `compileJava` is intentionally forced out-of-date to regenerate refmap output, so do not assume it will be a cheap
  up-to-date check.
- Soft integrations are mostly `compileOnly` dependencies plus runtime `ModList`/`CompatRegistry` checks. Libraries
  bundled through `mcLib` are shaded/relocated by `shadowJar`; do not add broad dependencies to the published jar
  accidentally.
- No Spotless/Checkstyle/PMD/Prettier-style lint or formatter config was found; rely on existing Java style and Gradle
  `check`.
