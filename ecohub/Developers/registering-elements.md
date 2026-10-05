---
title: "Registering Elements"
sidebar_position: 7
---

Libreforge only finds elements that you register. This page tells you how and when to register them. For the terms on this page, see the [Glossary](glossary).

## Register an element

Each element type has a registry. Call `register` on the registry, with your element.

| Element | Registry | Example |
| --- | --- | --- |
| Effect | `Effects` | `Effects.register(EffectRestoreHunger)` |
| Trigger | `Triggers` | `Triggers.register(TriggerEnterWater)` |
| Condition | `Conditions` | `Conditions.register(ConditionWearingElytra)` |
| Filter | `Filters` | `Filters.register(FilterMinVictimLevel)` |
| Mutator | `Mutators` | `Mutators.register(MutatorLocationUp)` |

## When to register

Register your elements before libreforge reads your configs. Libreforge reads configs when your plugin reloads, and this occurs after your plugin is enabled. Thus, register in `handleEnable`.

```kotlin
class MyPlugin : LibreforgePlugin() {
    override fun handleEnable() {
        Effects.register(EffectRestoreHunger)
        Effects.register(EffectNoFallDamage)
        Triggers.register(TriggerEnterWater)
        Conditions.register(ConditionWearingElytra)
        Filters.register(FilterMinVictimLevel)
        Mutators.register(MutatorLocationUp)
    }
}
```

Note: If a config uses an ID that is not registered, libreforge does not load that element.

## Rules for IDs

- Use only lowercase letters, numbers, and underscores. For example, `restore_hunger`.
- Make the ID unique for its element type. Do not use an ID that libreforge or a different plugin uses.
- Do not change an ID after users start to use it. If you must change it, keep the old ID in `aliases`:

```kotlin
object EffectRestoreHunger : Effect<NoCompileData>("restore_hunger") {
    override val aliases = setOf("give_food")
}
```

For permanent effects, `aliases` has one more use. Libreforge makes the identifiers from the first alias. Thus, a renamed permanent effect keeps the same identifiers.

## What libreforge does for you

When you register an element, libreforge:

- Registers effects and conditions as Bukkit listeners. You can add `@EventHandler` functions to them.
- Registers a trigger as a Bukkit listener when a config first uses it.
- Checks the arguments in each config against your `arguments` block. It writes a warning to the console for each argument that is not correct.

Filters and mutators are not listeners. Do not add `@EventHandler` functions to them.

## Next steps

- [Full Example: Permanent Effect](full-example-permanent-effect)
- [Full Example: Triggered Effect](full-example-triggered-effect)
