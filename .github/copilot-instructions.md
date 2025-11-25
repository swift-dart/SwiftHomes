# HomePlugin Development Guide

## Project Overview
HomePlugin is a Paper/Spigot 1.21+ Minecraft plugin providing GUI-based home management with permission-tiered limits. Built with Java 21, using Adventure API for modern text components and Bukkit's inventory system for chest-based GUIs.

## Architecture

### Core Component Flow
1. **HomePlugin** (main) → Initializes managers, registers commands/events
2. **HomeManager** → Handles home CRUD operations, permission-based limits, YAML persistence (`plugins/HomePlugin/homes/{uuid}.yml`)
3. **MessageManager** → Loads `messages.yml`, deserializes legacy `&` color codes to Adventure Components
4. **HomeGUI** → Creates inventory-based interfaces, tracks GUI state per UUID
5. **ChatInputListener** → Intercepts chat events for home naming workflow

### Data Storage Pattern
- Homes stored per-player: `plugins/HomePlugin/homes/{uuid}.yml`
- Each home serialized with: `name, world, x, y, z, yaw, pitch`
- In-memory cache: `Map<UUID, Map<String, Home>>` in HomeManager
- Auto-save on home set/delete, bulk save on disable

## Permission System (Highest-Wins)
```java
// HomeManager checks permissions 1-100, returns highest
homeplugin.homes.5    // 5 homes
homeplugin.homes.10   // 10 homes  
homeplugin.unlimited  // Integer.MAX_VALUE

// Fallback: config.yml `max-homes: 5` if no permission found
```

**Critical**: When adding home limit features, respect `getMaxHomes(Player)` logic scanning 1-100 range.

## Build System (Dual Setup)
- **Maven** (primary): `mvn clean package` → `target/HomePlugin-1.0.0.jar`
- **Gradle** (alternative): `gradle clean build` → `build/libs/HomePlugin.jar` (with shadowJar)
- Both use Paper API 1.21.3 (`compileOnly`/`provided` scope)
- Java 21 toolchain required

**Testing**: Copy JAR to server's `plugins/` folder, restart or `/reload confirm`

## Modern UI Patterns

### Adventure API Components
```java
// Colors.java defines hex palette
TextColor.fromHexString("#34D399") // SUCCESS
Component.text("✓ Success").color(Colors.SUCCESS).decoration(TextDecoration.ITALIC, false)
```

**Always** disable italic decoration (`.decoration(TextDecoration.ITALIC, false)`) - Minecraft defaults lore to italic.

### MessageManager Workflow
```java
// messages.yml: "commands.sethome.success: &aHome '{name}' set!"
messageManager.getCommandMessage("sethome", "success", 
    MessageManager.replacements("name", "myhouse"))
// Returns Component with {name} replaced, & codes converted
```

**Conventions**:
- `commands.{cmd}.{key}` for command messages
- `gui.{screen}.{element}` for GUI text
- Use `MessageManager.replacements(k, v, k, v...)` helper for placeholders

### GUI State Management
```java
// HomeGUI tracks active GUI type per player UUID
openGUIs.put(player.getUniqueId(), GUIType.SET_HOME_CONFIRM);

// Handle clicks via InventoryClickEvent, check GUI type:
if (openGUIs.get(uuid) == GUIType.DELETE_HOME) { ... }
```

**Pattern**: Black stained glass panes as fillers, concrete blocks as action buttons (green=confirm, red=cancel).

## Chat Input Flow (Two-Phase Home Creation)
1. Player runs `/sethome` (no args) → Opens GUI with green "Continue" button
2. Click green → Close GUI, request chat input via `ChatInputListener.requestHomeName()`
3. `ChatInputListener` intercepts next chat message (cancels event if pending input)
4. Validate name → Open confirmation GUI with entered name
5. Click confirm → Save home via `HomeManager.setHome()`

**Key**: `pendingInputs` Map tracks players awaiting input, prevents normal chat processing.

## Critical Validations
```java
// Home name rules (ChatInputListener)
homeName.matches("[a-zA-Z0-9_-]+")  // Only alphanumeric, hyphen, underscore
homeName.length() <= 16              // Max 16 chars

// Permission check before setting
if (!homeManager.canSetMoreHomes(player)) {
    // Block unless overwriting existing home
}
```

## Command Registration Pattern
```java
// HomePlugin.onEnable()
SetHomeCommand cmd = new SetHomeCommand(this);
cmd.setGUI(homeGUI);  // Inject GUI dependency
getCommand("sethome").setExecutor(cmd);
```

**Note**: Commands defined in `plugin.yml`, executors set programmatically with injected dependencies.

## Configuration Files
- **config.yml**: `max-homes` (default limit), `teleport-delay` (seconds)
- **messages.yml**: All user-facing text with `&` color codes
- **plugin.yml**: Commands, permissions, API version 1.21

Admin reload: `/homes reload` → Calls `MessageManager.reload()` (does NOT reload config.yml).

## Common Gotchas
1. **World validation**: `HomeManager.loadPlayerHomes()` skips homes with missing worlds (prevents NPE on multiverse removals)
2. **Case handling**: Home names stored lowercase (`homeName.toLowerCase()`), display name preserved in `Home.name`
3. **Teleport delay**: Currently in config but not implemented in codebase (TODO: async scheduler)
4. **Maven vs Gradle**: Both `pom.xml` and `build.gradle` exist; Maven is documented primary in BUILD.md
