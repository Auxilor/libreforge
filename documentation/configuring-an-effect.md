---
title: "Configuring an Effect"
sidebar_position: 4
---

Effects are the actions libreforge runs for you: dealing damage, spawning particles, giving money, sending messages, and more. This page explains how an effect is put together, the difference between **triggered** and **permanent** effects, and the **optional arguments** and **placeholders** you can layer on top.

There are two types of effect: **triggered** and **permanent**. They are configured the same way with a few differences. Triggered effects need a trigger to activate; permanent effects are always active as long as their conditions, if any, are met.

## Example effect config

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

This effect has a 25% chance to spawn 10 soul particles in the centre of a diamond ore when it is mined and the player is below Y level 10.

## Effects

An effect is the action that gets executed; it is the core functionality. Each effect has an ID that determines what it does and a set of args that configure how it behaves. You can find all available effects under "All Effects" on the sidebar.

```yaml
effects:
  - id: spawn_particle # The ID of the effect; find all effects under "All Effects" on the sidebar
    args: # The args for the effect (from the effect page); optional args can also go here (see below)
      amount: 10
      particle: soul
```

## Triggers

A trigger is the event or action that causes the effect to run, e.g. mining a block, attacking an entity, taking damage, or jumping. Triggered effects need at least one trigger to activate. Permanent effects do not use triggers, as they are always active when their conditions are met.

```yaml
    triggers: # The triggers that activate this effect (does not apply to permanent effects)
      - mine_block
```

You can find all available triggers here.

## Filters

Filters narrow down when an effect activates by restricting the trigger to specific targets. For example, you can filter a `mine_block` trigger to only apply when mining diamond ore, or a `melee_attack` trigger to only apply when attacking zombies. The available filters depend on the trigger being used.

```yaml
    filters: # The filters applied to the trigger
      blocks: # e.g. "blocks" on mine_block, or "entities" on melee_attack
        - diamond_ore
        - deepslate_diamond_ore
```

## Conditions

Conditions are requirements that must be met for the effect to activate. They check things like the player's Y level, the world they are in, whether they have a permission, and more. As well as each effect holder (e.g. Talisman, Reforge, Enchant) having its own conditions, you can specify a list of effect-specific conditions that work in exactly the same way.

```yaml
    conditions: # Optional; effect-specific conditions, working the same way as holder-level conditions
      - id: below_y
        args:
          y: 10
```

See [Configuring a Condition](configuring-a-condition) to understand how to configure conditions.

## Mutators

Mutators modify the data passed to an effect before it runs. They let you change parameters such as the location, the victim, or the player. For example, a `translate_location` mutator shifts where a particle spawns. Like effects and conditions, a mutator is an ID plus arguments.

```yaml
    mutators: # Optional; mutate the data sent to the effect, e.g. change the victim or location
      - id: translate_location # A mutator, like an effect or condition, is an ID plus arguments
        args:
          add_x: 0.5
          add_y: 0.5
          add_z: 0.5
```

## Optional arguments

All of these go inside the `args` section of an effect.

```yaml
args:
  chance: 50 # Optional; the chance of this effect activating, as a percentage (defaults to 100)
  cost: 200 # Optional; the cost to activate this effect, requires Vault (defaults to 0)
  every: 3 # Optional; the effect activates every x times (defaults to always)
  require: '%ecobits_crystals% > 4' # Optional; an expression that must be true for the effect to run
  delay: 20 # Optional; ticks to wait before executing the effect (defaults to 0)
  mana_cost: 10 # Optional; the mana cost to activate this effect, requires AuraSkills (defaults to 0)
  run-order: early # Optional; the order the effect runs in: start, early, normal, late, or end
```

For EcoSkills, you can use `<magic>_cost` (e.g. `mana_cost`) to specify a magic cost (defaults to 0).

Effects have default run orders (so they work together properly), but `run-order` overrides them, for example to make `add_damage` (defaults to `late`) run before `damage_multiplier` (defaults to `normal`).

### Cooldown

```yaml
args:
  cooldown: 10 # The cooldown between activations, in seconds (defaults to 0)
  cooldown_group: magic_abilities # Optional; the cooldown group, otherwise the cooldown is for this effect only
  send_cooldown_message: true # Optional; whether the cooldown message is sent
  cooldown_message: "Custom cooldown message with %seconds% left" # Optional; a custom cooldown message
  cooldown_effects: # Optional; effects to run while on cooldown
    - id: send_message
      args:
        message: "You are on cooldown! Try again in &a%seconds%&r seconds."
```

### Repeat

```yaml
args:
  repeat: # Optional; the effect activates repeatedly, delaying between each repeat if delay is set
    times: 5 # How many times the effect repeats
    start: -10 # The initial value of the %repeat_count% placeholder
    increment: 10 # How much the count increases (or decreases) by on each repeat
```

If the effect has any mutators, they run again for each repeat. This provides new placeholders: `%repeat_times%`, `%repeat_start%`, `%repeat_increment%`, and `%repeat_count%`.

### Price

```yaml
args:
  price: # Optional; the price to activate this effect, supports money, items, points, second currencies, etc.
    value: 100 * %player_y% # The value of the price, supports math expressions
    type: crystals # The price type
    display: "&b%value% Crystals ❖" # The display name of the price
```

Read more about the price system here: [Prices](https://hub.auxilor.io/wiki/eco/the-price-lookup-system).

### Advanced options

```yaml
args:
  filters_before_mutation: true # Optional; run filters on the un-mutated data instead of after mutation (defaults to false)
  disable_antigrief_check: true # Optional; disable antigrief plugin checks for this effect (defaults to false)
  custom_<id>: # Optional; use a custom effect argument (see Custom Arguments)
    <arg 1>: <value>
    <arg 2>: <value>
```

See [Custom Arguments](custom-arguments) to define your own reusable arguments.

## Placeholders

:::info Values are expressions
Any numeric value (integer or decimal) can be a mathematical expression involving placeholders. Permanent effects evaluate the expression on activation; triggered effects evaluate it on each trigger. Only use placeholders with numeric values, or you will get unexpected behaviour.
:::

For example, you can make the chance depend on Y level with `chance: 100 - %player_y%`. If the victim is a player, you can also supply any placeholder prefixed with `victim_` (e.g. `%victim_player_y%`).

There are extra placeholders passed in that you can use:

| Placeholder | Value | Aliases |
| --- | --- | --- |
| `%trigger_value%` | The value passed by the trigger (e.g. the amount of damage dealt; see here). | `%triggervalue%`, `%trigger%`, `%value%`, `%tv%`, `%v%`, `%t%` |
| `%alt_trigger_value%` | The alt-value passed by the trigger (see here). | `%alttriggervalue%`, `%alttrigger%`, `%altvalue%`, `%atv%`, `%av%`, `%at%` |
| `%player%` | The player's name | |
| `%player_uuid%` | The player's UUID | |
| `%victim_health%` | The victim's health | |
| `%victim_max_health%` | The victim's max health | |
| `%distance%` | The distance between the player and the victim | |
| `%victim_level%` | The victim's level (requires LevelledMobs) | |
| `%hits%` | The number of times the player has hit the victim | |
| `%text%` | The message text from the trigger, e.g. a chat message | `%string%`, `%message%` |
| `%location_x%` | The X coordinate of the location | `%loc_x%`, `%x%` |
| `%location_y%` | The Y coordinate of the location | `%loc_y%`, `%y%` |
| `%location_z%` | The Z coordinate of the location | `%loc_z%`, `%z%` |
| `%location_block_x%` | The X coordinate of the block location | `%loc_b_x%`, `%block_x%`, `%bx%` |
| `%location_block_y%` | The Y coordinate of the block location | `%loc_b_y%`, `%block_y%`, `%by%` |
| `%location_block_z%` | The Z coordinate of the block location | `%loc_b_z%`, `%block_z%`, `%bz%` |
| `%location_world%` | The world name of the location | `%loc_w%`, `%world%` |

## Load weight

All configs load alphabetically by default. If a config depends on another, for example an EcoItems item crafted with another EcoItems item, add `load-weight: <weight>`. All configs default to a load weight of 100 and load in ascending order, so a config with a load weight of 10 loads before one with 20.

<hr/>

## Where to go next

- **Conditions:** [Configuring a Condition](configuring-a-condition) to gate effects behind requirements.
- **Chains:** [Configuring an Effect Chain](configuring-a-chain) to group effects together.
- **Effects:** browse "All Effects" on the sidebar for the full list of actions.
