# HomePlugin - Complete Permission Reference

## Quick Permission Guide

### Basic Commands (Default: Everyone)
```
homeplugin.sethome    - Use /sethome command
homeplugin.home       - Use /home command
homeplugin.delhome    - Use /delhome command
homeplugin.homes      - Use /homes command
```

### Admin Commands (Default: OPs only)
```
homeplugin.admin      - Use /homes reload and admin functions
```

### Home Limits (Give ONE per player/group)

**Pre-defined Limits:**
```
homeplugin.homes.1    - 1 home
homeplugin.homes.2    - 2 homes
homeplugin.homes.3    - 3 homes
homeplugin.homes.4    - 4 homes
homeplugin.homes.5    - 5 homes (default in config)
homeplugin.homes.10   - 10 homes
homeplugin.homes.15   - 15 homes
homeplugin.homes.20   - 20 homes
homeplugin.homes.25   - 25 homes
homeplugin.homes.50   - 50 homes
homeplugin.unlimited  - Unlimited homes
```

**Custom Limits:**
You can create any limit between 1-100:
```
homeplugin.homes.7    - 7 homes
homeplugin.homes.42   - 42 homes
homeplugin.homes.99   - 99 homes
```

## How It Works

The plugin uses a **highest-wins** system:
- If a player has `homeplugin.homes.3` and `homeplugin.homes.10`, they get **10 homes**
- If a player has no home limit permission, they use the `max-homes` value from config.yml (default: 5)
- `homeplugin.unlimited` overrides all numeric limits

## LuckPerms Setup Examples

### Default Players (3 homes)
```bash
lp group default permission set homeplugin.sethome
lp group default permission set homeplugin.home
lp group default permission set homeplugin.delhome
lp group default permission set homeplugin.homes
lp group default permission set homeplugin.homes.3
```

### VIP Rank (10 homes)
```bash
lp group vip parent set default
lp group vip permission set homeplugin.homes.10
```

### Admin Rank (Unlimited homes + reload)
```bash
lp group admin parent set vip
lp group admin permission set homeplugin.unlimited
lp group admin permission set homeplugin.admin
```

### Give a Specific Player More Homes
```bash
lp user PlayerName permission set homeplugin.homes.20
```

## permissions.yml Example

```yaml
groups:
  default:
    permissions:
      homeplugin.sethome: true
      homeplugin.home: true
      homeplugin.delhome: true
      homeplugin.homes: true
      homeplugin.homes.3: true
      
  vip:
    inheritance:
      - default
    permissions:
      homeplugin.homes.10: true
      
  mvp:
    inheritance:
      - vip
    permissions:
      homeplugin.homes.25: true
      
  admin:
    inheritance:
      - mvp
    permissions:
      homeplugin.unlimited: true
      homeplugin.admin: true
```

## Testing Permissions

To test what limit a player has:
1. Have them run `/homes`
2. They'll see "Your homes: X/Y" where Y is their limit
3. If they have unlimited, it shows "Your homes: X (Unlimited)"

## Common Issues

**Player has multiple home limit permissions:**
- The highest permission wins automatically
- Example: Player with both `.homes.5` and `.homes.20` gets 20 homes

**Player has no home limit permission:**
- They use the `max-homes` value from `config.yml`
- Default is 5 homes

**Player is OP but can't reload:**
- OPs automatically get `homeplugin.admin` permission
- If it's not working, check your permissions plugin settings
