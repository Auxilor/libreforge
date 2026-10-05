---
title: "Creating a Trigger"
sidebar_position: 3
---

A trigger is an event that makes triggered effects run. This page tells you how to make one. For the terms on this page, see the [Glossary](glossary).

## Make a trigger

1. Make a Kotlin `object` that extends `Trigger`. Give the ID to the constructor.
2. Set `parameters` to the values that the trigger sends. Effects that need a value that is not in this set cannot use the trigger.
3. Add a Bukkit `@EventHandler` function.
4. In the function, call `dispatch` with the dispatcher and the trigger data.

```kotlin
object TriggerEnterWater : Trigger("enter_water") {
    override val description = "Fires when the player moves into water."

    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION,
        TriggerParameter.EVENT
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: PlayerMoveEvent) {
        val to = event.to
        if (event.from.block.isLiquid || !to.block.isLiquid) {
            return
        }

        val player = event.player

        this.dispatch(
            player.toDispatcher(),
            TriggerData(
                player = player,
                location = to,
                event = event
            )
        )
    }
}
```

## Trigger parameters

Send a value in `TriggerData` for each parameter in `parameters`. Do not add a parameter that you do not send.

| Parameter | `TriggerData` property | Contains |
| --- | --- | --- |
| `PLAYER` | `player` | The player who caused the trigger. |
| `VICTIM` | `victim` | The other entity. For example, the mob that the player hit. |
| `BLOCK` | `block` | The block. |
| `EVENT` | `event` | The Bukkit event. Effects such as `cancel_event` use it. |
| `LOCATION` | `location` | The location. |
| `PROJECTILE` | `projectile` | The projectile. |
| `VELOCITY` | `velocity` | The velocity. |
| `ITEM` | `item` | The item. |
| `TEXT` | `text` | A text value. For example, a chat message. |
| `VALUE` | `value` | A number. For example, the damage or the amount of XP. |
| `ALT_VALUE` | `altValue` | A second number. |

Note: `LOCATION`, `VELOCITY` and `ITEM` have default values from the player or the victim. If the trigger sends `PLAYER`, an effect that needs `LOCATION` can also use the trigger.

## The dispatcher

The first argument of `dispatch` is the dispatcher. Libreforge runs the active effects of this dispatcher only.

- For a player, use `player.toDispatcher()`.
- For a mob, use `entity.toDispatcher()`.

## Things to know

- Libreforge registers the listener only when a config uses the trigger. A trigger that no config uses costs nothing.
- `dispatch` does nothing if the dispatcher has no effects for the trigger. You do not have to check this yourself.
- Keep the event handler fast. Do simple checks first and return early.

## Next steps

- Register the trigger. See [Registering Elements](registering-elements).
- See a trigger used with an effect: [Full Example: Triggered Effect](full-example-triggered-effect).
