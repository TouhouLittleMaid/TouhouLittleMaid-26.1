# AGENTS.md - Touhou Little Maid

## Language policy

- 默认使用简体中文回答。
- 除非我明确要求英文，否则不要切换英文叙述。
- 代码、命令、报错、API 名称保持原文，不要强行翻译。
- 提问澄清时也使用中文。

## Tech Stack

- **NeoForge** 26.1.2.36-beta (Minecraft 26.1.2 = 1.21.1)
- **Java 25** toolchain (CI builds with JDK 21)
- **Gradle 9.4.1** with NeoGradle moddev plugin v2.0.141
- **Shadow** v9.4.1 for jar-in-jar library bundling
- **Mixin** compatibilityLevel `JAVA_25`

## Build Commands

```bash
./gradlew build                    # full build (includes shadowJar)
./gradlew clean                    # clean build artifacts
./gradlew runClient                # launch client (username: tartaric_acid)
./gradlew runServer                # launch dedicated server
./gradlew runData                  # run data generators
./gradlew runGameTestServer        # game test server
./gradlew test                     # JUnit 4 tests (note: no test sources exist currently)
```

**Important**: `assemble` depends on `shadowJar`. The default `jar` task is disabled. The output artifact is
`build/libs/touhoulittlemaid-<version>-all.jar`.

`compileJava` is configured to never be up-to-date to force Mixin refmap regeneration.

## Project Structure

```
src/main/java/com/github/tartaricacid/touhoulittlemaid/
├── TouhouLittleMaid.java          # @Mod main class (both sides)
├── TouhouLittleMaidClient.java    # @Mod client-only class (dist = Dist.CLIENT)
├── init/                          # DeferredRegister registries (blocks, items, entities, etc.)
│   └── registry/                  # Sub-registries (CommandRegistry, CompatRegistry, CommonRegistry, etc.)
├── entity/                        # Custom entities + AI tasks
├── ai/                            # LLM AI chat system
├── client/                        # Client-only code
│   ├── init/                      # Client-side registration (renderers, GUIs, key bindings)
│   ├── event/                     # Client event handlers (14+ classes)
│   ├── model/                     # Entity models
│   ├── renderer/                  # Entity/block renderers
│   └── gui/                       # GUI screens
├── event/                         # Server-side event handlers
│   └── maid/                      # Maid-specific event handlers (12+ classes)
├── network/                       # Network packets (RegisterPayloadHandlersEvent pattern)
│   └── message/                   # Payload implementations
│       └── ai/                    # AI-related packets
├── block/                         # Custom blocks
├── tileentity/                    # Block entities
├── item/                          # Custom items
├── inventory/                     # Container/screen menus
├── api/                           # Public API (ILittleMaid extension interface)
├── compat/                        # Soft compat with other mods (aquaculture, create, etc.)
├── config/                        # Config (GeneralConfig, ServerConfig)
├── datagen/                       # Data generators
├── datapack/                      # Datapack-related code
├── mixing/                        # Mixin classes (common + client subdirs)
│   └── plugin/MixinPlugin.java    # Mixin plugin (conditional InvTweaks compat)
├── molang/                        # MoLang expression interpreter
├── command/                       # Commands
├── crafting/                      # Custom crafting recipes
├── data/                          # Data attachment types
├── advancements/                  # Custom advancements
├── geckolib3/                     # GeckoLib integration
├── world/                         # World-gen, POI types
├── debug/                         # Dev/debug utilities (condition: TouhouLittleMaid.DEBUG)
├── loot/                          # Loot modifiers/conditions
└── util/                          # Utility classes
```

Resources:

```
src/main/resources/
├── META-INF/neoforge.mods.toml    # Mod metadata (uses Gradle property expansion)
├── META-INF/accesstransformer.cfg # Access transformers
├── touhou_little_maid.mixins.json  # Mixin config
├── assets/                        # Client assets (textures, models, sounds, lang)
├── data/                          # Data pack data (recipes, loot tables, tags)
└── pack.mcmeta                    # pack_format: 34 (1.21.1)

src/generated/resources/           # Generated resources (data gen output)
└── data/                          # Auto-generated datapack data
```

## Registration Pattern

All registries use `DeferredRegister` in `init/` package classes. They are registered in
`TouhouLittleMaid.initRegister()`:

```java
InitEntities.ENTITY_TYPES.register(eventBus);
InitItems.ITEMS.

register(eventBus);
// ... etc
```

When adding new content:

1. Add the `DeferredRegister` field to the appropriate `Init*` class
2. Register its `Supplier` entries
3. Add the `.register(eventBus)` call in `TouhouLittleMaid.initRegister()`

## Event Bus Subscribers

Lifecycle event handlers use the standalone `@EventBusSubscriber` annotation (from
`net.neoforged.fml.common.EventBusSubscriber`), **not** `@Mod.EventBusSubscriber`. Key subscriber classes:

- `InitEntities` — entity attributes, spawn placements
- `CommonRegistry` — `FMLCommonSetupEvent` (manager init, pack reload, YSM compat)
- `CompatRegistry` — `InterModEnqueueEvent` (soft compat)
- `DatapackRegistry` — `AddServerReloadListenersEvent`
- `DataGenerator` — `GatherDataEvent` (data providers)
- `client/init/ClientSetupEvent` — `FMLClientSetupEvent`, overlays, reload listeners

## Network

Uses modern NeoForge `RegisterPayloadHandlersEvent` pattern. All packets defined in `NetworkHandler.registerPacket()`.

Packets extend `CustomPacketPayload`, use `STREAM_CODEC`, and handle via a static `handle()` method.

Sending packets: use `NetworkHandler.sendToClientPlayer()`, `sendToNearby()` helpers.

## Mixins

- Config: `touhou_little_maid.mixins.json`
- Plugin: `MixinPlugin` — conditionally applies `client.compat.InvTweaksMixin` when InvTweaks is loaded on client
- Common mixins in `mixin/`, client-only in `mixin/client/`, accessors in `mixin/accessor/`
- Compatibility level: `JAVA_25`

## Key Conventions

- **Encoding**: UTF-8 (enforced in JavaCompile tasks)
- **Package root**: `com.github.tartaricacid.touhoulittlemaid`
- **Mod ID**: `touhou_little_maid`
- **Soft compat**: Mods are included as `compileOnly` dependencies. Check `CompatRegistry` to see which mod's load
  status is tracked. Use `LoadingModList` or `ModList.get().isLoaded()` at runtime.
- **DEBUG flag**: `TouhouLittleMaid.DEBUG` — `true` outside production (dev env). Debug utilities in `debug/` package
  should gate on this.
- **Jar-in-jar**: `mcLib` configuration collects libraries (opus decoder, mp3 decoder, snakeyaml); Shadow plugin
  relocates them under `com.github.tartaricacid.touhoulittlemaid.libs.*`
- **No README** exists in the repo
- **.gitignore excludes** `src/test`, so test sources under `src/test/` are not tracked
- **Project uses Eclipse** format (`.classpath`, `.project`, `.settings/`) but also generates IDEA config
