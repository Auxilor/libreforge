---
title: "Configuring an Effect Chain"
sidebar_position: 2
---

Effect chains are groups of effects executed together. This page explains why chains exist, the difference between **reusable** and **inline** chains, how to **call** them, and the **run types** that control how their effects fire.

Chains are most useful for chance-based effects with several parts. Chance is calculated independently per effect, so without a chain, particles and messages could fire when the other effects don't, and vice versa.

Effects in a chain run in isolation: a mutator on one effect applies only to that effect. To apply a mutator to every effect, set it on the parent effect. Delays work the same way, e.g. a delay of 2 on one effect won't hold up the rest of the chain.

Chains can also reuse complex logic via custom arguments. These work like regular placeholders and are referenced inside the chain with `%<id>%`, for example `%size%` for a `size` argument.

There are two ways to create chains: **reusable chains** in a shared config file, and **inline chains** defined directly inside an effect holder.

## Reusable chains

Reusable chains live in `chains.yml` in `/plugins/libreforge`. They are universally accessible: you can use them in Enchants, Skills, Jobs, or any other effect holder. This is ideal when you want the same chain in multiple places.

You don't specify triggers in the chain itself; those are handled by the `run_chain` effect when you call the chain.

```yaml
chains:
  - id: mining_effect # The unique ID used to reference this chain
    effects: # The effects to run in the chain
      - id: play_sound # First effect: play a sound
        args:
          sound: BLOCK_AMETHYST_CLUSTER_BREAK
          pitch: 0.7
          volume: 10
      - id: spawn_particle # Second effect: spawn particles
        args:
          particle: soul
          amount: 10
        mutators:
          - id: translate_location
            args:
              add_x: 0.5
              add_y: 0.5
              add_z: 0.5
```

You can add or remove as many chains as you want.

### Calling a chain

To call a reusable chain, use the `run_chain` effect. Triggers, filters, and [optional args](configuring-an-effect#optional-arguments) are specified here, not in the chain itself.

```yaml
- id: run_chain
  args:
    chain: mining_effect # The ID of the chain to run
    chance: 50 * (%player_health% / 20) # Optional args are supported here
    cooldown: 2
  triggers:
    - mine_block
  filters:
    blocks:
      - diamond_ore
      - emerald_ore
      - ancient_debris
```

### Custom chain arguments

You can pass custom arguments into a chain using `chain_args`. These are available as placeholders inside the chain's effects.

```yaml
- id: run_chain
  args:
    chain: <chain id>
    chain_args:
      strength: "%player_y% * 100" # Referenced as %strength% inside the chain
```

## Inline chains

If you don't want to reuse a chain, or you prefer effects defined directly inside the effect holder, define chains inline. Inline chains also support custom arguments, just like reusable chains.

```yaml
effects:
  - triggers:
      - mine_block
    filters:
      blocks:
        - diamond_ore
        - emerald_ore
        - ancient_debris
    args:
      every: 3 # Optional args are supported here too
    effects:
      - id: play_sound # First effect: play a sound
        args:
          sound: BLOCK_AMETHYST_CLUSTER_BREAK
          pitch: 0.7
          volume: 10
      - id: spawn_particle # Second effect: spawn particles
        args:
          particle: soul
          amount: 10
        mutators:
          - id: translate_location
            args:
              add_x: 0.5
              add_y: 0.5
              add_z: 0.5
```

## Run types

Effect chains support several run types that control how their effects execute:

- **normal:** all effects run sequentially, one after another (default).
- **cycle:** only one effect runs, cycling through each effect each time the chain is triggered.
- **random:** only one effect runs, chosen at random each time the chain is triggered.

Set the run type with the `run-type` argument:

```yaml
effects:
  - triggers:
      - alt_click
    args:
      run-type: random # The run type: normal, cycle, or random
      chance: 30
    effects:
      - <effect 1>
      - <effect 2>
      - <effect 3>
```

### Weighted random chains

With the `random` run type, you may want some effects to occur more often than others, e.g. a higher chance of dropping an Iron Ingot and a lower chance of dropping a Diamond. Set a `weight` on each effect:

```yaml
effects:
  - triggers:
      - mine_block
    args:
      run-type: random
    effects:
      - id: drop_item
        args:
          item: diamond
        weight: 10 # Lower weight = less likely to be chosen
      - id: drop_item
        args:
          item: iron_ingot
        weight: 60 # Higher weight = more likely to be chosen
```

Weight is calculated as `<weight of element> / <sum of all weights>`.

<hr/>

## Where to go next

- **Effects:** [Configuring an Effect](configuring-an-effect) for how each effect inside a chain is built.
- **Conditions:** [Configuring a Condition](configuring-a-condition) to gate chained effects behind requirements.
- **Triggers:** [All Triggers](https://plugins.auxilor.io/effects/all-triggers) for every event a chain can hook into.
