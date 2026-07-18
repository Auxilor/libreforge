---
title: "Configuring a Condition"
sidebar_position: 5
---

Conditions are requirements that must be met for an effect to activate. They let you control when effects run by checking things like the player's permission, Y level, world, or health. This page covers the shared **inverse** argument, **not-met lines** and **not-met effects** for feedback, and **victim conditions** for checking the target. Like effects and mutators, a condition is an ID plus arguments.

You can find all available conditions under "All Conditions" on the sidebar.

```yaml
- id: has_permission
  args:
    permission: "ecomc.rank.mvp" # The required permission
    inverse: true # If the player should *not* have the permission
```

## Inverse

Every condition has an optional `inverse` argument. It negates the condition, so it is only true when the base condition is false.

```yaml
- id: on_fire
  args:
    inverse: true # Optional; negates the condition, i.e. only true when the base condition is false
```

## Not-met lines

If the condition is for an item plugin (EcoEnchants, EcoItems, Reforges, Talismans, or EcoArmor), you can add lore lines shown to the player when they don't meet the condition.

```yaml
- id: has_permission
  args:
    permission: "ecomc.rank.mvp"
    not-met-lines: # Optional; lore lines shown when the condition is not met (item plugins only)
      - "&cYou need &bMVP&c rank to use &7Crystal Finder"
      - "&cBuy it at &astore.ecomc.net"
```

## Not-met effects

If the condition is effect-specific (i.e. in the `conditions:` section of an effect rather than on the main holder conditions), you can specify not-met effects. These run when the condition is not met but a player tries to activate the effect by invoking the trigger.

```yaml
effects:
  - id: give_money
    args:
      amount: 100
    conditions:
      - id: has_permission
        args:
          permission: "ecomc.rank.mvp"
        not-met-effects: # Optional; effects to run when the condition is not met
          - id: send_message
            args:
              message: "&cYou need &bMVP&c rank to use &7Crystal Finder&c, buy it at &astore.ecomc.net&c!"
    triggers:
      - break_block
```

Here, MVP players get $100 for breaking a block, while non-MVP players are told to buy the rank when they try. This is an alternative to not-met lines.

## Victim conditions

You can check whether the victim (player or entity) meets conditions before running effects using the [`victim_conditions`](https://hub.auxilor.io/wiki/libreforge/victim_conditions?category=filters) filter. Combine these with normal conditions to build intricate effects with comprehensive criteria.

```yaml
effects:
  - id: give_money
    args:
      amount: 100
    filters:
      victim_conditions: # Check conditions on the victim (player or entity)
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

Here, the player gains $100 when they attack a player or entity wielding a diamond sword in their main hand, while the player is below Y level 0.

<hr/>

## Where to go next

- **Effects:** [Configuring an Effect](configuring-an-effect) for where conditions sit inside an effect.
- **Chains:** [Configuring an Effect Chain](configuring-a-chain) to gate grouped effects.
- **Conditions:** browse "All Conditions" on the sidebar for the full list.
