---
title: "Full Example: Permanent Effect"
sidebar_position: 8
---

This page shows all the steps to add a permanent effect and to give it to players. For the terms on this page, see the [Glossary](glossary).

In this example, players with the permission `myplugin.vip` get a holder named `vip`. Server owners write the effects of the holder in `config.yml`. The new effect `no_fall_damage` stops fall damage.

## Overview

1. Make the effect.
2. Register the effect.
3. Make the holder from the config.
4. Register a holder provider. This gives the holder to players.
5. Write the config.

Libreforge then turns the effect on and off for you.

## Step 1: Make the effect

```kotlin
package com.example.myplugin

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.map.listMap
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.effects.Identifiers
import com.willfp.libreforge.effects.ProviderBinding
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageEvent
import java.util.UUID

object EffectNoFallDamage : Effect<NoCompileData>("no_fall_damage") {
    override val description = "Stops fall damage to the player."

    override val shouldReload = false

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

For more about each part, see [Creating an Effect](creating-an-effect).

## Steps 2 to 4: Register the effect, make the holder, and give it to players

```kotlin
package com.example.myplugin

import com.willfp.libreforge.Holder
import com.willfp.libreforge.SimpleHolder
import com.willfp.libreforge.SimpleProvidedHolder
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.conditions.Conditions
import com.willfp.libreforge.effects.Effects
import com.willfp.libreforge.loader.LibreforgePlugin
import com.willfp.libreforge.registerSpecificHolderProvider
import org.bukkit.entity.Player

class MyPlugin : LibreforgePlugin() {
    // Made again on each reload, so changes to config.yml apply.
    // It is null until the first reload, and the provider must accept this.
    private var vipHolder: Holder? = null

    override fun handleEnable() {
        // Step 2: Register the effect.
        Effects.register(EffectNoFallDamage)

        // Step 4: Give the holder to each player who has the permission.
        registerSpecificHolderProvider<Player>(id = "myplugin:vip") { player ->
            val holder = vipHolder

            if (holder != null && player.hasPermission("myplugin.vip")) {
                listOf(SimpleProvidedHolder(holder))
            } else {
                emptyList()
            }
        }
    }

    override fun handleReload() {
        // Step 3: Make the holder from config.yml.
        val context = ViolationContext(this, "config.yml")

        vipHolder = SimpleHolder(
            this.namespacedKeyFactory.create("vip"),
            Effects.compile(configYml.getSubsections("vip.effects"), context),
            Conditions.compile(configYml.getSubsections("vip.conditions"), context)
        )
    }
}
```

## Step 5: Write the config

```yaml
# config.yml
vip:
  effects:
    - id: no_fall_damage
  conditions: []
```

The effect has no `triggers`, so it is permanent.

## What occurs at runtime

1. A player with `myplugin.vip` joins. Libreforge asks the holder provider for the holders of the player.
2. The provider gives the `vip` holder. The conditions of the holder are true.
3. Libreforge calls `onEnable` of `no_fall_damage`. The player gets no fall damage.
4. The player loses the permission. Libreforge asks the provider again and gets no holders.
5. Libreforge calls `onDisable`. The player gets fall damage again.

When your plugin reloads, libreforge turns off all effects. Then it turns them on again with the new holders. You do not have to do this yourself.

## When libreforge asks the provider again

Libreforge asks the holder provider again in these cases:

- The items of the player change, the player respawns, or the player goes to a different world.
- A fixed time passes. For players, this is 80 ticks (4 seconds) by default.
- Your code tells libreforge to do it.

A permission change does not cause an event. To apply the change immediately, tell libreforge after you change the permission:

```kotlin
player.toDispatcher().refreshHolders()
```

You can also set the time and the events for each provider:

```kotlin
// Also import com.willfp.libreforge.HolderChange.
registerSpecificHolderProvider<Player>(
    id = "myplugin:vip",
    maxAge = { 200 }, // Ask again after 200 ticks. Return null to never ask on a timer.
    invalidatedBy = setOf(HolderChange.Respawn) // Ask again only after these changes.
) { player ->
    // ...
}
```

Caution: The provider must return the holders in the same order each time. If the order changes, libreforge can turn effects off and on again when nothing changed.
