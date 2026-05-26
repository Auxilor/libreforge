---
title: Configuring an Effect Chain
sidebar_position: 2
---

## The Basics

Effect chains are groups of effects that can be executed together. This is very useful if you want to create a chance-based effect with several components: chance is calculated independently on each effect, so without chains, particles and messages could send when the other effects don't activate, and vice-versa.

Effects in chains run in isolation, so applying a mutator to one effect in the chain will apply it only to that effect — however, you can specify a mutator on the parent effect which will be applied to all effects in the chain. The same works for delays, e.g. if an effect in a chain has a delay of 2, it won't hold up other effects down the chain.

Effect chains are also useful to re-use more complex logic, via custom arguments that you can specify. These work like regular placeholders, and you reference them in your chains with `%<id>%`, for example `%size%` if you had a size argument.

There are two ways to create chains: **Reusable Chains** defined in a shared config file, and **Inline Chains** defined directly inside an effect holder.

## Reusable Chains

Reusable chains are defined in `chains.yml` in `/plugins/libreforge`. They are universally accessible — you can use them in Enchants, Skills, Jobs, or any other effect holder. This is great if you want to use the same chain in multiple places.

You don't need to specify triggers in your chain; these are handled by the `run_chain` effect when you call the chain.

### The Chain Layout

```yaml
chains:
  - id: <chain id> # The unique ID used to reference this chain
    effects: # The list of effects to run in the chain
      - <effect 1>
      - <effect 2>
      - <effect 3>
```

### Example Chain Config

```yaml
chains:
  - id: mining_effect # The unique ID of this chain
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

You can add or remove as many chains as you want.

### Calling a Chain

To call a reusable chain, use the `run_chain` effect. Triggers, filters, and [optional args](https://plugins.auxilor.io/effects/configuring-an-effect#optional-arguments) are specified here, not in the chain itself.

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

### Custom Chain Arguments

You can pass custom arguments into a chain using `chain_args`. These are available as placeholders inside the chain's effects.

```yaml
- id: run_chain
  args:
    chain: <chain id>
    chain_args:
      strength: "%player_y% * 100" # Referenced as %strength% inside the chain
      # Add whichever arguments you use in your chain
```

## Inline Chains

If you don't want to re-use a chain, or if you prefer having effects specified directly inside the effect holder, you can define chains inline instead. Inline chains also support custom arguments, just like reusable chains.

### The Inline Layout

```yaml
effects:
  - <effect 1>
  - <effect 2>
  - <effect 3>
triggers:
  - mine_block
args:
  every: 3 # Optional args are supported here: https://plugins.auxilor.io/effects/configuring-an-effect#optional-arguments
```

### Example Inline Chain

```yaml
effects:
  - triggers:
      - mine_block
    filters:
      blocks:
        - diamond_ore
        - emerald_ore
        - ancient_debris
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

## Run Types

Effect chains support several run types that control how effects in the chain are executed:

- **normal**: All effects in the chain will be run sequentially, one after another (default)
- **cycle**: Only one effect will be run, cycling through each effect each time the chain is triggered
- **random**: Only one effect will be run, chosen at random each time the chain is triggered

To specify the run type, add the `run-type` argument:

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

### Weighted Random Chains

When using the `random` run type, you may want certain effects to occur more frequently than others — for example, a higher chance of dropping an Iron Ingot and a lower chance of dropping a Diamond.

To do this, specify a `weight` on each effect in the chain:

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