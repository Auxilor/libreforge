---
title: "Configuring a Condition"
sidebar_position: 3
---

## The Basics

Conditions are requirements that must be met for an effect to activate. They allow you to control when effects run by checking things like the player's permission, Y level, world, health, and more. Like effects and mutators, conditions consist of an ID and arguments.

You can find all available conditions under "All Conditions" on the sidebar.

## Example Condition Config

```yaml
- id: has_permission
  args:
    permission: "ecomc.rank.mvp" # The required permission
    inverse: true # If the player should *not* have the permission
```

## Understanding all the sections

### The Inverse Argument

All conditions have an optional `inverse` argument. This negates the condition, so it will only be true when the base condition is false.

```yaml
- id: on_fire
  args:
    inverse: true # (Optional) Negates the condition, i.e. only true when the base condition is false
```

### Not-Met Lines

If the condition is for an item plugin (EcoEnchants, EcoItems, Reforges, Talismans, or EcoArmor), you can add lore lines to be shown to the player when they don't meet the condition.

```yaml
- id: has_permission
  args:
    permission: "ecomc.rank.mvp"
    not-met-lines: # (Optional) Lore lines shown when the condition is not met (item plugins only)
      - "&cYou need &bMVP&c rank to use &7Crystal Finder"
      - "&cBuy it at &astore.ecomc.net"
```

### Not-Met Effects

If the condition is effect-specific (i.e. in the `conditions: []` section of an effect rather than on the main holder conditions), you can specify not-met-effects. These are effects that run when the condition is not met but a player tries to activate the effect (invokes the trigger).

```yaml
effects:
  - id: give_money
    args:
      amount: 100
    conditions:
      - id: has_permission
        args:
          permission: "ecomc.rank.mvp"
        not-met-effects: # (Optional) Effects to run when the condition is not met
          - id: send_message
            args:
              message: "&cYou need &bMVP&c rank to use &7Crystal Finder&c, buy it at &astore.ecomc.net&c!"
    triggers:
      - break_block
```

In this example, MVP players get $100 for breaking a block, whereas non-MVP players are told to buy the rank when they try to break a block. This functions as an alternative to not-met-lines.

### Victim Conditions

You can check if the victim (player/entity) meets conditions before running effects by using the [`victim_conditions`](https://plugins.auxilor.io/effects/all-filters/victim_conditions) filter. You can combine these with usual conditions to create intricate effects with comprehensive lists of criteria to be met.

```yaml
effects:
  - id: give_money
    args:
      amount: 100
    filters:
      victim_conditions: # Check conditions on the victim (player/entity)
        - id: in_mainhand
          args:
            items:
              - DIAMOND_SWORD
    conditions: # Check conditions on the player
      - id: below_y
        args:
          y: 0
    triggers:
      - melee_attack
```

In this example, the player gains $100 when they attack a player/entity wielding a diamond sword in their main-hand, whilst the player is below Y level 0.
