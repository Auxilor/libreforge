---
title: "Creating a Condition"
sidebar_position: 4
---

A condition is a check that must be true before an effect can run or stay on. This page tells you how to make one. For the terms on this page, see the [Glossary](glossary).

## Make a condition

1. Make a Kotlin `object` that extends `Condition<T>`. Give the ID to the constructor.
2. Set `T` to `NoCompileData` if you do not need compile data.
3. Add the arguments that the user must write.
4. Override `isMet`. Return `true` if the condition is true for the dispatcher.

```kotlin
object ConditionWearingElytra : Condition<NoCompileData>("wearing_elytra") {
    override val description = "Passes when the player wears an elytra."

    // The result changes only when the items of the player change.
    override val invalidatedBy = setOf(HolderChange.Items)

    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ): Boolean {
        val player = dispatcher.get<Player>() ?: return false
        return player.inventory.chestplate?.type == Material.ELYTRA
    }
}
```

## When libreforge checks a condition

Libreforge checks the conditions of permanent effects again and again. The `invalidatedBy` property tells libreforge when to do this.

| `invalidatedBy` value | Libreforge checks the condition |
| --- | --- |
| `null` (default) | At a fixed interval. Use this if you do not know which events change the result. |
| A set of `HolderChange` values | Only after one of these changes occurs. This uses less server time. |

The built-in changes are:

| `HolderChange` | Occurs when |
| --- | --- |
| `Items` | The inventory, the armour, or the held item changes. |
| `Respawn` | The player respawns. |
| `WorldChange` | The player goes to a different world. |
| `custom<E> { event -> dispatcher }` | A Bukkit event of type `E` occurs. Return the dispatcher to check, or `null` to ignore the event. |

```kotlin
// Check again only when the player changes game mode.
override val invalidatedBy = setOf(
    HolderChange.custom<PlayerGameModeChangeEvent> { it.player.toDispatcher() }
)
```

Caution: If you set `invalidatedBy`, it must contain every change that can change the result. If it does not, a permanent effect can stay on when the condition is false.

Note: A condition that reads a placeholder from a config is always checked at the fixed interval. Placeholders can change at any time.

## Things to know

- Triggered effects check their conditions each time the trigger occurs. `invalidatedBy` does not change this.
- The user can write `inverse: true` and `not-met-lines` in the config. Libreforge does this for you.
- Keep `isMet` fast. Libreforge calls it frequently.

## Next steps

- Register the condition. See [Registering Elements](registering-elements).
