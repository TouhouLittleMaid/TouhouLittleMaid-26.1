# AGENTS.md

## Language policy

- 默认使用简体中文回答。
- 除非我明确要求英文，否则不要切换英文叙述。
- 代码、命令、报错、API 名称保持原文，不要强行翻译。
- 提问澄清时也使用中文。

Repository-specific guidance for coding agents working in `TouhouLittleMaid-1.21`.
All items below were verified against the current repository contents.

## 1) Project snapshot

- Build system: **Gradle Wrapper** (`gradlew`, `gradlew.bat`)
- Language toolchain: **Java 21** (`build.gradle`)
- Mod platform: **NeoForge** (`net.neoforged.moddev` plugin)
- Packaging: `assemble` depends on `shadowJar`
- Test dependency: **JUnit 4.13.2** (`testImplementation`)

## 2) Cursor/Copilot rule files

- `.cursor/rules/`: not found
- `.cursorrules`: not found
- `.github/copilot-instructions.md`: not found

So there are no extra assistant policy files to inherit; follow existing code patterns.

## 3) Build / test / run commands

Run commands from repository root.
Use `.bat` on Windows and non-`.bat` equivalents on macOS/Linux.

### Core commands

- `./gradlew.bat clean`
- `./gradlew.bat build`
- `./gradlew.bat check`
- `./gradlew.bat test`
- `./gradlew.bat assemble`

### NeoForge dev commands

- `./gradlew.bat runClient`
- `./gradlew.bat runServer`
- `./gradlew.bat runGameTestServer`
- `./gradlew.bat runData`

### Task discovery

- `./gradlew.bat tasks --all`
- `./gradlew.bat help --task test`

### Single-test commands (important)

Gradle test filtering is supported via `--tests`.

- Single class:
  - `./gradlew.bat test --tests "com.github.tartaricacid.touhoulittlemaid.ExampleTest"`
- Single method:
  - `./gradlew.bat test --tests "com.github.tartaricacid.touhoulittlemaid.ExampleTest.shouldDoThing"`
- Method wildcard:
  - `./gradlew.bat test --tests "*ExampleTest.should*"`

Current state note: `src/test/java` is not present right now.

## 4) Lint/format reality

- No Spotless config found.
- No Checkstyle config found.
- No `.editorconfig` found.
- No dedicated `lint` Gradle task.
- Use `check` as verification umbrella task.

Practical rule: avoid style churn; keep formatting aligned with nearby code.

## 5) Style conventions from source code

### Formatting

- 4-space indentation.
- K&R braces.
- Blank lines between logical blocks.
- Wrapped long builder/fluent/record declarations.

### Imports

Observed grouping pattern:
1) project/local packages
2) third-party + Minecraft/NeoForge
3) `java.*`/`javax.*`
4) static imports last

Avoid reordering imports unless required by your edit.

### Naming

- Types (`class`, `record`, `enum`): `PascalCase`
- Methods/fields/locals/params: `camelCase`
- Constants (`static final`): `UPPER_SNAKE_CASE`
- Packages: lowercase, feature-oriented

### Types and modeling

- Packet payloads often use Java `record`.
- Packet classes typically expose:
  - `public static final Type<...> TYPE`
  - `public static final StreamCodec<...> STREAM_CODEC`
- Prefer explicit, readable types in shared/core code.

### Nullability

- Some packages declare defaults in `package-info.java`:
  - `@ParametersAreNonnullByDefault`
  - `@MethodsReturnNonnullByDefault`
- Use `@Nullable` explicitly where null is valid.
- Prefer guard clauses + early returns for invalid/null state.

### Error handling

- Catch specific exception types (`IOException`, parse exceptions, etc.).
- Log with useful context; do not silently swallow broadly.
- If intentionally ignoring exceptions, keep scope narrow and comment why.

### Logging

- Main logger pattern:
  - `public static final Logger LOGGER = LogManager.getLogger(MOD_ID);`
- Use parameterized logging (`{}` placeholders).
- Marker-based logging appears in resource-loading paths.

### Comments

- Comments are concise and intent-focused.
- Chinese comments are common; keep local language/style consistent.
- Do not add obvious comments that restate code.

## 6) Architecture hints

- Entry points: `TouhouLittleMaid`, `TouhouLittleMaidClient`
- Common package roles:
  - `network.message.*`: networking payloads/handlers
  - `client.*`: rendering/UI/client resources
  - `util.*`: shared helpers
  - `tileentity.*`, `entity.*`, `world.*`: gameplay systems

When adding code, place it in the existing feature namespace.

## 7) Agent workflow checklist

Before editing:
1. Find a nearby analogous implementation and mirror its style.
2. Confirm the right Gradle task(s) (`tasks --all` if uncertain).
3. Determine whether changes are client-only, server-only, or shared.

After editing:
1. Run targeted verification first (filtered `test --tests ...` when applicable).
2. Run `./gradlew.bat check` before handoff.
3. Ensure diff does not contain unrelated formatting churn.

## 8) Packet-specific checklist

For changes under `network.message`:
1. Keep packet `TYPE` identifier stable and unique.
2. Maintain `STREAM_CODEC` encode/decode symmetry.
3. Keep side checks explicit (`isServerbound` / `isClientbound`).
4. Follow existing enqueue/handler flow patterns.
5. Keep boundary nullability checks explicit.

## 9) Do not assume

- Auto-format/lint tooling exists (it currently does not).
- Tests exist for every module.
- Cursor/Copilot policy files exist (none found right now).

## 10) Quick commands

- Build: `./gradlew.bat build`
- Check: `./gradlew.bat check`
- Test all: `./gradlew.bat test`
- Test class: `./gradlew.bat test --tests "pkg.ClassName"`
- Test method: `./gradlew.bat test --tests "pkg.ClassName.methodName"`
- Run client: `./gradlew.bat runClient`
- Run data gen: `./gradlew.bat runData`

Keep this file updated when tooling/rules/project conventions change.
