# SwiftHomes

A modern, GUI-based home management plugin for Paper/Spigot Minecraft servers with a fully customizable message system.

## Features

- 🏠 Set multiple homes with custom names
- 🎨 Modern, intuitive chest GUI interface
- 🔧 Fully customizable messages via `messages.yml`
- 🎯 Permission-based home limits
- ⚡ Teleport delay system
- 💬 Chat-based home naming
- 🌈 Modern color scheme

## Commands

| Command | Description | Usage |
|---------|-------------|-------|
| `/sethome` | Open GUI to set a home at your location | `/sethome` or `/sethome <name>` |
| `/home` | Teleport to a home or open selection GUI | `/home` or `/home <name>` |
| `/delhome` | Delete a home or open deletion GUI | `/delhome` or `/delhome <name>` |
| `/homes` | List all your homes | `/homes` |
| `/homes help` | Show detailed help menu | `/homes help` or `/home help` |
| `/homes reload` | Reload messages.yml (admin only) | `/homes reload` |

## Permissions

### Basic Permissions

| Permission | Description | Default |
|------------|-------------|---------|
| `swifthomes.sethome` | Allows setting homes | `true` |
| `swifthomes.home` | Allows teleporting to homes | `true` |
| `swifthomes.delhome` | Allows deleting homes | `true` |
| `swifthomes.homes` | Allows listing homes | `true` |
| `swifthomes.admin` | Allows reloading messages and admin functions | `op` |

### Home Limit Permissions

The plugin uses a **highest-wins** permission system. Give players one of these permissions to set their home limit:

| Permission | Homes Allowed |
|------------|---------------|
| `swifthomes.homes.1` | 1 home |
| `swifthomes.homes.2` | 2 homes |
| `swifthomes.homes.3` | 3 homes |
| `swifthomes.homes.4` | 4 homes |
| `swifthomes.homes.5` | 5 homes |
| `swifthomes.homes.10` | 10 homes |
| `swifthomes.homes.15` | 15 homes |
| `swifthomes.homes.20` | 20 homes |
| `swifthomes.homes.25` | 25 homes |
| `swifthomes.homes.50` | 50 homes |
| `swifthomes.unlimited` | Unlimited homes |

**Note:** The plugin checks permissions from 1-100, so you can create custom permissions like `swifthomes.homes.7` for 7 homes. The highest permission the player has will be used.

## Configuration

### config.yml

```yaml
# Maximum number of homes per player (default if no permission)
max-homes: 5

# Teleport delay in seconds (0 for instant)
teleport-delay: 3
```

### messages.yml

All plugin messages are fully customizable in `messages.yml`. Use `&` for color codes:
- `&a` = Green
- `&c` = Red
- `&b` = Aqua
- `&f` = White
- `&7` = Gray
- `&e` = Yellow
- etc.

After editing `messages.yml`, use `/homes reload` to apply changes without restarting the server.

## Installation

1. Download `swifthomes.jar`
2. Place it in your server's `plugins/` folder
3. Restart or reload your server
4. Configure `config.yml` and `messages.yml` in `plugins/swifthomes/`

## Building from Source

Requirements:
- Java 21 JDK
- Gradle 8.5+

```bash
cd swifthomes
gradle clean build
```

The compiled JAR will be in `build/libs/swifthomes.jar`

## Permissions Examples

### LuckPerms Examples

```bash
# Give a player 3 homes
lp user PlayerName permission set swifthomes.homes.3

# Give a group 10 homes
lp group vip permission set swifthomes.homes.10

# Give unlimited homes to admins
lp group admin permission set swifthomes.unlimited
```

### permissions.yml Example

```yaml
groups:
  default:
    permissions:
      - swifthomes.sethome
      - swifthomes.home
      - swifthomes.delhome
      - swifthomes.homes
      - swifthomes.homes.3
      
  vip:
    permissions:
      - swifthomes.homes.10
      
  admin:
    permissions:
      - swifthomes.unlimited
      - swifthomes.admin
```

## Home Storage

Homes are stored per-player in YAML files:
```
plugins/swifthomes/homes/<player-uuid>.yml
```

Each home stores:
- Name
- World
- X, Y, Z coordinates
- Yaw and Pitch

## Support

For issues, questions, or contributions, please open an issue on GitHub.

## License

This plugin is open source. Feel free to modify and distribute.
