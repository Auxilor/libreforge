---
title: "Creating a Filter"
sidebar_position: 5
---

A filter is a check on the data of one trigger. For example, "only when the victim is a zombie". This page tells you how to make one. For the terms on this page, see the [Glossary](glossary).

A filter is different from a condition:

- A **condition** checks the dispatcher. For example, "the player is below Y 10".
- A **filter** checks the trigger data. For example, "the block that the player broke is diamond ore".

## How users write a filter

A filter has no `args` section. The ID of the filter is the key, and the user writes one value:

```yaml
filters:
  min_victim_level: 5
```

## Make a filter

1. Make a Kotlin `object` that extends `Filter<T, V>`. Give the ID to the constructor.
2. Set `T` to `NoCompileData` if you do not need compile data.
3. Set `V` to the type of the value. For example, `Double`, `Boolean`, or `Collection<String>`.
4. Override `getValue`. Read the value from the config at `key`.
5. Override `isMet`. Return `true` to let the effect run. Return `false` to stop it.

```kotlin
object FilterMinVictimLevel : Filter<NoCompileData, Double>("min_victim_level") {
    override val description = "Passes when the victim player has at least this XP level."

    override val valueType = ArgType.DOUBLE

    override fun getValue(config: Config, data: TriggerData?, key: String): Double {
        return config.getDoubleFromExpression(key, data)
    }

    override fun isMet(data: TriggerData, value: Double, compileData: NoCompileData): Boolean {
        // If there is no victim player, do not block the effect.
        val victim = data.victim as? Player ?: return true

        return victim.level >= value
    }
}
```

## Things to know

- Libreforge does the inverse for you. The user can write `not_min_victim_level` to get the opposite result.
- `data` is `null` in `getValue` when the config loads. Your code must accept `null`.
- Return `true` when the data that you check is not present. Then the filter does not stop effects on triggers that do not send that data.
- By default, a filter checks the trigger data after the mutators change it.

## Next steps

- Register the filter. See [Registering Elements](registering-elements).
