---
title: Configuring an Effect
sidebar_position: 1
---

## The Basics
First, you need to know of the different types of Effects: Triggered and Permanent. These are configured similarly but there are a few differences.
The main difference is that all Triggered effects require a [trigger](https://plugins.auxilor.io/effects/all-triggers) to activate, Permanent effects are always active if all conditions (optional) are met.

## Example Effect Config

```yaml
effects:
  - id: spawn_particle
    args:
      amount: 10
      particle: soul
      chance: 25
    triggers:
      - mine_block
    filters:
      blocks:
        - diamond_ore
        - deepslate_diamond_ore
    mutators:
      - id: translate_location
        args:
          add_x: 0.5
          add_y: 0.5
          add_z: 0.5
    conditions:
      - id: below_y
        args:
          y: 10
```

The example effect: 25% chance to spawn 10 soul particles in the center of a diamond ore when its mined and the player is below Y level 10.

## Understanding all the sections

### The Effects Section

An effect is the action that gets executed — it's the core functionality, such as dealing damage, spawning particles, giving money, sending messages, and more. Each effect has an ID that determines what it does and a set of args that configure how it behaves. You can find all available effects under "All Effects" on the sidebar.

```yaml
effects:
  - id: spawn_particle # The ID of the effect, find all effects under "All Effects" on the sidebar
    args: # The args for the effect (from the effect page), additional optional args can also be placed here (see below)
      amount: 10
      particle: soul
```

### The Triggers Section

A trigger is the event or action that causes the effect to run — for example, mining a block, attacking an entity, taking damage, or jumping. Triggered effects require at least one trigger to activate. Permanent effects do not use triggers, as they are always active when their conditions are met.

```yaml
    triggers: # The list of triggers that activate this effect (does not apply on permanent effects)
      - mine_block
```

You can find all available triggers [here](https://plugins.auxilor.io/effects/all-triggers).

### The Filters Section

Filters narrow down when an effect should activate by restricting the trigger to specific targets. For example, you can filter a `mine_block` trigger to only apply when mining diamond ore, or filter a `melee_attack` trigger to only apply when attacking zombies. The available filters depend on the trigger being used.

```yaml
    filters: # The list of filters to be applied on the trigger
      blocks: # e.g. "blocks" filter on mine_block trigger, or "entities" filter on melee_attack trigger
        - diamond_ore
        - deepslate_diamond_ore
```

### The Conditions Section

Conditions are requirements that must be met for the effect to activate. They check things like the player's Y level, the world they are in, whether they have a permission, and more. As well as each effect holder (e.g. Talisman, Reforge, Enchant) having its own conditions, you can specify a list of effect-specific conditions that work in exactly the same way.

```yaml
    conditions: # (Optional) Effect-specific conditions, work the same way as holder-level conditions
      - id: below_y
        args:
          y: 10
```

Check out [Configuring a Condition](https://plugins.auxilor.io/effects/configuring-a-condition) to understand how to configure conditions.

### The Mutators Section

Mutators modify the data passed to an effect before it runs. They allow you to change parameters such as the location, the victim, or the player. For example, you can use a `translate_location` mutator to shift where a particle spawns. Like effects and conditions, a mutator consists of an ID and arguments.

```yaml
    mutators: # (Optional) Mutate the data sent to the effect, e.g. change the victim, location, etc.
      - id: translate_location # A mutator, like an effect or condition, consists of an ID and arguments
        args:
          add_x: 0.5
          add_y: 0.5
          add_z: 0.5
```

## Optional Arguments

All of these are placed inside the `args` section of an effect.

### Chance

```yaml
args:
  chance: 50 # (Optional) The chance of this effect activating, as a percentage (defaults to 100)
```

### Cost

```yaml
args:
  cost: 200 # (Optional) The cost required to activate this effect, requires Vault (defaults to 0)
```

### Every

```yaml
args:
  every: 3 # (Optional) The effect will activate every x times (defaults to always)
```

### Require

```yaml
args:
  require: '%ecobits_crystals% > 4' # (Optional) Require an expression to be true for the effect to run
```

### Cooldown

```yaml
args:
  cooldown: 10 # The cooldown between effect activations, in seconds (defaults to 0)
  cooldown_group: magic_abilities # (Optional) The cooldown group, if not specified the cooldown will be for this effect only
  send_cooldown_message: true # (Optional) If the cooldown message should be sent
  cooldown_message: "Custom cooldown message with %seconds% left" # (Optional) A custom cooldown message
  cooldown_effects: # (Optional) Effects to run if on cooldown
    - id: send_message
      args:
        message: "You are on cooldown! Try again in &a%seconds%&r seconds."
```

### Mana Cost

```yaml
args:
  mana_cost: 10 # (Optional) The mana cost required to activate this effect, requires AuraSkills (defaults to 0)
```

For EcoSkills, you can use `<magic>_cost` (e.g. `mana_cost`) to specify a magic cost. (defaults to 0)

### Delay

```yaml
args:
  delay: 20 # (Optional) The amount of ticks to wait before executing the effect (defaults to 0)
```

### Repeat

```yaml
args:
  repeat: # (Optional) The effect will activate repeatedly, delaying between each repeat if delay is set
    times: 5 # How many times the effect should be repeated
    start: -10 # The initial value of the %repeat_count% placeholder
    increment: 10 # How much the count should be increased (or decreased) by on each repeat
```

If the effect has any mutators, they will run again for each repeat.

This provides new placeholders: `%repeat_times%`, `%repeat_start%`, `%repeat_increment%`, and `%repeat_count%`.

### Price

```yaml
args:
  price: # (Optional) The price required to activate this effect, supports money, items, points, second currencies, etc.
    value: 100 * %player_y% # The value of the price, supports math expressions
    type: crystals # The price type
    display: "&b%value% Crystals ❖" # The display name of the price
```

Read more about the price system here: [Prices](https://plugins.auxilor.io/all-plugins/prices)

### Run Order

```yaml
args:
  run-order: early # (Optional) The order the effect should run in: start, early, normal, late, or end
```

Effects have default run orders (used to make effects work together properly), but this option allows for overriding them, for example to make `add_damage` (defaults to `late`) run before `damage_multiplier` (defaults to `normal`).

### Advanced Options

```yaml
args:
  filters_before_mutation: true # (Optional) Run filters on the un-mutated data instead of after mutation (defaults to false)
  disable_antigrief_check: true # (Optional) Disable antigrief plugin checks for this effect (defaults to false)
  custom_<id>: # (Optional) Use a custom effect argument: https://plugins.auxilor.io/effects/custom-arguments
    <arg 1>: <value>
    <arg 2>: <value>
```

## Placeholders

**Any numeric value (integer, decimal) can be a mathematical expression involving placeholders!**

For example, you can specify the chance to be dependent on your y level: as in `chance: 100 - %player_y%` - permanent effects will evaluate the expression on activation, and triggered effects will evaluate it on each trigger. 
Make sure you only use placeholders with numeric values, as you will get weird behavior otherwise.

If the victim is a player, you can supply any placeholder prefixed with `victim_` (e.g. `%victim_player_y%`) as well.

There are also extra placeholders passed in that you can use:

| Placeholder           | Value                                                                                                                               | Aliases                                                                    |
|-----------------------|-------------------------------------------------------------------------------------------------------------------------------------|----------------------------------------------------------------------------|
| `%trigger_value%`     | The value passed by the trigger (e.g. the amount of damage dealt; see [here](https://plugins.auxilor.io/effects/all-triggers)).     | `%triggervalue%`, `%trigger%`, `%value%`, `%tv%`, `%v%`, `%t%`             |
| `%alt_trigger_value%` | The alt-value passed by the trigger (e.g. the amount of damage dealt; see [here](https://plugins.auxilor.io/effects/all-triggers)). | `%alttriggervalue%`, `%alttrigger%`, `%altvalue%`, `%atv%`, `%av%`, `%at%` |
| `%player%`            | The player's name                                                                                                                   |                                                                            |
| `%player_uuid%`       | The player's UUID                                                                                                                   |                                                                            |
| `%victim_health%`     | The victim's health                                                                                                                 |                                                                            |
| `%victim_max_health%` | The victim's max health                                                                                                             |                                                                            |
| `%distance%`          | The distance between the player and the victim                                                                                      |                                                                            |
| `%victim_level%`      | The victim's level (**Requires LevelledMobs**)                                                                                      |                                                                            |
| `%hits%`              | The amount of times the player has hit the victim                                                                                   |                                                                            |
| `%text%`              | The message text from the trigger, for example a chat message                                                                       | `%string%`, `%message%`                                                    |
| `%location_x%`        | The X coordinate of the location                                                                                                    | `%loc_x%`, `%x%`                                                           |
| `%location_y%`        | The Y coordinate of the location                                                                                                    | `%loc_y%`, `%y%`                                                           |
| `%location_z%`        | The Z coordinate of the location                                                                                                    | `%loc_z%`, `%z%`                                                           |
| `%location_block_x%`  | The X coordinate of the block location                                                                                              | `%loc_b_x%`, `%block_x%`, `%bx%`                                           |
| `%location_block_y%`  | The Y coordinate of the block location                                                                                              | `%loc_b_y%`, `%block_y%`, `%by%`                                           |
| `%location_block_z%`  | The Z coordinate of the block location                                                                                              | `%loc_b_z%`, `%block_z%`, `%bz%`                                           |
| `%location_world%`    | The world name of the location                                                                                                      | `%loc_w%`, `%world%`                                                       |

## Load Weight

All configs are loaded alphabetically by default. However, if you have a config that depends on another one, for example an EcoItems item that's crafted with another EcoItems item, you can add `load-weight: <weight>`. All configs have a default load weight of 100, and it's loaded in ascending order, so a config with a load weight of 10 is loaded before a load weight of 20.
