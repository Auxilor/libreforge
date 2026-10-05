---
title: "Creating an Effect"
sidebar_position: 2
---

An effect is the action that libreforge does. This page tells you how to make a triggered effect and a permanent effect. For the terms on this page, see the [Glossary](glossary).

## Choose the effect type

The `parameters` property sets the type of the effect.

- If `parameters` is empty, the effect is **permanent**. Override `onEnable` and `onDisable`.
- If `parameters` has one or more values, the effect is **triggered**. Override `onTrigger`.

Do not make one effect that is both types.

## Make a triggered effect

1. Make a Kotlin `object` that extends `Effect<T>`. Give the ID to the constructor.
2. Set `T` to `NoCompileData` if you do not need compile data.
3. Set `parameters` to the trigger parameters that the effect needs.
4. Add the arguments that the user must write.
5. Override `onTrigger`. Return `true` if the effect ran. Return `false` if it did not run.

```kotlin
object EffectRestoreHunger : Effect<NoCompileData>("restore_hunger") {
    override val description = "Gives food points to the player."

    // Libreforge only lets users attach this effect to triggers that send a player.
    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        require(
            "amount",
            "You must specify the amount of food points!",
            description = "The number of food points to give.",
            type = ArgType.EXPRESSION,
            example = "4"
        )
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val player = data.player ?: return false

        val amount = config.getIntFromExpression("amount", data)
        player.foodLevel = (player.foodLevel + amount).coerceAtMost(20)

        return true
    }
}
```

Note: Use `config.getDoubleFromExpression(key, data)` or `config.getIntFromExpression(key, data)` to read numbers. These functions calculate placeholders and math, for example `%level% * 2`.

## Make a permanent effect

1. Make a Kotlin `object` that extends `Effect<T>`. Do not set `parameters`.
2. Override `onEnable`. Apply the change to the dispatcher here.
3. Override `onDisable`. Remove the change here. Remove only the change that this effect made.
4. Use `identifiers` to find the change. Each dispatcher gets different identifiers for the same effect.

```kotlin
object EffectNoFallDamage : Effect<NoCompileData>("no_fall_damage") {
    override val description = "Stops fall damage to the player."

    // The change does not use the config, so libreforge does not have to reload it.
    override val shouldReload = false

    // The change does not use the item or the slot of the holder.
    override val providerBinding = ProviderBinding.NONE

    // For each dispatcher, the identifiers of each active copy of this effect.
    private val active = listMap<UUID, UUID>()

    override fun onEnable(
        dispatcher: Dispatcher<*>,
        config: Config,
        identifiers: Identifiers,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ) {
        active[dispatcher.uuid].add(identifiers.uuid)
    }

    override fun onDisable(dispatcher: Dispatcher<*>, identifiers: Identifiers, holder: ProvidedHolder) {
        active[dispatcher.uuid].remove(identifiers.uuid)
    }

    // Effects are listeners. Libreforge registers this handler for you.
    @EventHandler(ignoreCancelled = true)
    fun handle(event: EntityDamageEvent) {
        if (event.cause != EntityDamageEvent.DamageCause.FALL) {
            return
        }

        if (active[event.entity.uniqueId].isNotEmpty()) {
            event.isCancelled = true
        }
    }
}
```

Caution: Two holders can give the same effect to one player. Keep a list for each dispatcher, as in the example. If you keep only one value, the first `onDisable` removes the change for both holders.

### Optional settings for permanent effects

| Setting | Default | Use |
| --- | --- | --- |
| `shouldReload` | `true` | Set to `false` if the change does not use values from the config that can change. |
| `onReload` | Calls `onDisable`, then `onEnable` | Override to update the change in one step. For example, change the amount of an attribute modifier. |
| `providerBinding` | `SLOT` | Set to `ITEM` if the effect changes the item that gives the holder. Set to `NONE` if the effect does not use the item or the slot. |
| `isApplied` | Returns `null` | Return `false` if the change is not present now, for example because a different plugin removed it. Libreforge then applies it again. Return `null` if you cannot know. This function runs frequently, so make it fast. |

## Compile data

Use compile data to do slow work one time, when the config loads.

1. Set `T` to your data type.
2. Override `makeCompileData`. Read the config and return the data.
3. Use `compileData` in `onTrigger` or `onEnable`.

```kotlin
object EffectGiveItem : Effect<TestableItem>("give_custom_item") {
    override val parameters = setOf(TriggerParameter.PLAYER)

    override fun makeCompileData(config: Config, context: ViolationContext): TestableItem {
        return Items.lookup(config.getString("item"))
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: TestableItem): Boolean {
        val player = data.player ?: return false
        player.inventory.addItem(compileData.item)
        return true
    }
}
```

## Next steps

- Register the effect. See [Registering Elements](registering-elements).
- See a full example: [Full Example: Permanent Effect](full-example-permanent-effect) or [Full Example: Triggered Effect](full-example-triggered-effect).
