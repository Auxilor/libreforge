---
title: "Creating a Mutator"
sidebar_position: 6
---

A mutator changes the data of one trigger before the effect gets it. For example, it moves the location up, or it makes the player the victim. This page tells you how to make one. For the terms on this page, see the [Glossary](glossary).

## Make a mutator

1. Make a Kotlin `object` that extends `Mutator<T>`. Give the ID to the constructor.
2. Set `T` to `NoCompileData` if you do not need compile data.
3. Add the arguments that the user must write.
4. Override `mutate`. Return a changed copy of the data. Use `data.copy(...)`.
5. If the mutator adds a value to the data, set `parameterTransformers`.

```kotlin
object MutatorLocationUp : Mutator<NoCompileData>("location_up") {
    override val description = "Moves the location up by a number of blocks."

    override val arguments = arguments {
        require(
            "blocks",
            "You must specify the number of blocks!",
            description = "The number of blocks to move the location up.",
            type = ArgType.EXPRESSION,
            example = "2"
        )
    }

    override fun mutate(data: TriggerData, config: Config, compileData: NoCompileData): TriggerData {
        val location = data.location ?: return data

        return data.copy(
            location = location.clone().add(0.0, config.getDoubleFromExpression("blocks", data), 0.0)
        )
    }
}
```

Caution: Do not change the objects in the data directly. For example, do not call `data.location.add(...)`. Other effects use the same objects. Make a copy, as in the example.

## Parameter transformers

Libreforge uses the trigger parameters to find the effects that a trigger can run. If your mutator adds a value, tell libreforge. Then users can attach more effects to the trigger.

| Function | Use |
| --- | --- |
| `A becomes B` | The mutator makes value `B` from value `A`. If the trigger sends `A`, it now also sends `B`. |
| `adds(B)` | The mutator always sets value `B`. |

```kotlin
// This mutator sets the victim to the player.
override val parameterTransformers = parameterTransformers {
    TriggerParameter.PLAYER becomes TriggerParameter.VICTIM
}
```

Do not set `parameterTransformers` if the mutator only changes a value that is already present. The example `location_up` does not set it.

## Next steps

- Register the mutator. See [Registering Elements](registering-elements).
