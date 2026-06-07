---
title: "Item Levels"
sidebar_position: 9
---

Item levels let an item gain **XP** and **level up** as it is used, instead of having a value set directly. Each item level is a config file that defines how much XP each level needs and what happens on level-up, and you grant XP with the `level_item` **effect**. This page covers creating one, naming it, its structure, and the placeholders it exposes.

## Quick start

1. Open the `/plugins/libreforge/levels/` folder.
2. Copy `_example.yml` and rename it, e.g. `pickaxe.yml`. The file name (without `.yml`) is the level's ID.
3. Set your XP requirements with either `xp-formula` or a `requirements` list.
4. Add the effects you want under `level-up-effects`.
5. On an item, add the `level_item` effect with your level's ID so it gains XP (see [Granting XP](#granting-xp)).
6. Run `/libreforge reload`, then use the item and watch it gain XP and level up.

:::tip
`_example.yml` is included as a reference and is **never loaded**, so copy or rename it to make a real item level. You can also organise item levels into subfolders inside `levels/`, and they'll still load.
:::

## Naming and IDs

The file name without `.yml` is the item level's ID. A file named `pickaxe.yml` has the ID `pickaxe`, which you reference as `id: pickaxe` in the `level_item` effect and in placeholders like `%libreforge_item_level_pickaxe%`.

:::warning ID rules
IDs may only contain lowercase letters, numbers, and underscores (a-z, 0-9, _). No spaces, capitals, or hyphens, or the item level will not load.
:::

## The structure of an item level

| Part | What it controls |
| --- | --- |
| **XP requirements** | How much XP each level needs |
| **Level-up effects** | What runs when the item levels up |
| **Granting XP** | How an item earns XP towards the level |

```yaml
# === XP requirements: how much XP each level needs ===
# Option 1: a formula for infinite levels
xp-formula: (2 ^ %level%) * 25 # XP needed for the next level; %level% is the current level
max-level: 10 # Optional; the maximum level

# Option 2: a fixed list instead of xp-formula, one entry per level
# requirements:
#   - 50
#   - 100
#   - 200
#   - 400

# === Level-up effects: what runs when the item levels up ===
level-up-effects: # %level% is the level the item reached
  - id: send_message
    args:
      message: "&fYou leveled up to &a%level%&f!"
  - id: play_sound
    args:
      sound: entity_player_levelup
      volume: 1.0
      pitch: 1.5
```

### XP requirements

There are two ways to set how much XP each level needs: a formula for infinite levels, or a fixed list. Use one or the other.

```yaml
xp-formula: (2 ^ %level%) * 25 # XP needed for the next level; %level% is the current level
max-level: 10 # Optional; the maximum level
```

```yaml
requirements: # XP for each level, in order; the list length sets the max level
  - 50
  - 100
  - 200
  - 400
```

### Level-up effects

These effects run each time the item levels up. The `%level%` placeholder is the level the item just reached.

```yaml
level-up-effects:
  - id: send_message
    args:
      message: "&fYou leveled up to &a%level%&f!"
```

:::danger Effects are their own system
Effects are configured the same way everywhere in libreforge, so they are documented separately.

- [Configuring an Effect](configuring-an-effect)
- [Configuring an Effect Chain](configuring-a-chain)
:::

### Granting XP

The level config only defines requirements and rewards; an item earns XP with the `level_item` effect. Add it to the item that should level up, passing your level's ID and the XP to grant. Here is an example EcoItems item using the `example` level:

```yaml
item:
  item: diamond_pickaxe hide_attributes unbreakable efficiency:5 blast_mining:3
  display-name: "&eLevellable Pickaxe &8- &6%libreforge_item_level_example_numeral%"
  lore:
    - "&fCurrently on level &a%libreforge_item_level_example%"
    - "&fXP: &a%libreforge_item_xp_example%&8/&a%libreforge_item_xp_required_example% &f(&a%libreforge_item_progress_example%%&f)"
  craftable: false
  recipe: [ ]

slot: mainhand

effects:
  - id: level_item # Grants XP to the item level
    args:
      id: example # The item level ID (the level file name without .yml)
      xp: "%v%" # XP granted per trigger; %v% is the trigger value
    triggers:
      - mine_block

conditions: [ ]
```

## Internal placeholders

Swap `<id>` for your item level's ID. Add `_numeral` to the end of any placeholder to get the value as a roman numeral.

| Placeholder | Value |
| --- | --- |
| `%libreforge_item_xp_<id>%` | The current XP |
| `%libreforge_item_level_<id>%` | The current level |
| `%libreforge_item_xp_required_<id>%` | The XP required to level up |
| `%libreforge_item_progress_<id>%` | Progress towards the next level, as a percentage |
| `%libreforge_item_data_<key>%` | The data value |

:::tip Troubleshooting
- **Item level not loading?** Check the file name is lowercase letters, numbers, and underscores only, and that it isn't named `_example.yml`.
- **Item never levels up?** Make sure the item has a `level_item` effect whose `id` matches the level file name, and that the effect has a trigger.
- **Placeholders showing raw text?** Confirm the `<id>` in the placeholder matches the level file name exactly.
:::

<hr/>

## Where to go next

- **Effects:** [Configuring an Effect](configuring-an-effect) to build the `level_item` effect and your level-up effects.
- **Points:** [The Points System](points) for the related per-item data system.
- **Placeholders:** [Custom Placeholders](custom-placeholders) to reuse level expressions across configs.
