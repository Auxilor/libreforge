---
title: "Full Example: Triggered Effect"
sidebar_position: 9
---

This page shows all the steps to add a trigger and a triggered effect, and to give them to players. For the terms on this page, see the [Glossary](glossary).

In this example, your plugin has quests. When a player completes a quest, the new trigger `complete_quest` occurs. The new effect `restore_hunger` gives food points. Server owners connect the two in `config.yml`.

## Overview

1. Make the event that your plugin calls.
2. Make the trigger.
3. Make the effect.
4. Register the trigger and the effect.
5. Make the holder from the config, and give it to players.
6. Write the config.

## Step 1: Make the event

If your plugin already has an event, use it. Skip this step.

```kotlin
package com.example.myplugin

import org.bukkit.entity.Player
import org.bukkit.event.HandlerList
import org.bukkit.event.player.PlayerEvent

class QuestCompleteEvent(
    player: Player,
    val questId: String,
    val reward: Double
) : PlayerEvent(player) {
    override fun getHandlers() = handlerList

    companion object {
        private val handlerList = HandlerList()

        @JvmStatic
        fun getHandlerList() = handlerList
    }
}
```

Call the event when a player completes a quest:

```kotlin
Bukkit.getPluginManager().callEvent(QuestCompleteEvent(player, "slay_dragon", 50.0))
```

## Step 2: Make the trigger

The trigger sends the quest ID as text and the reward as a number.

```kotlin
package com.example.myplugin

import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.EventHandler

object TriggerCompleteQuest : Trigger("complete_quest") {
    override val description = "Fires when the player completes a quest."

    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION,
        TriggerParameter.EVENT,
        TriggerParameter.TEXT,
        TriggerParameter.VALUE
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: QuestCompleteEvent) {
        val player = event.player

        this.dispatch(
            player.toDispatcher(),
            TriggerData(
                player = player,
                location = player.location,
                event = event,
                text = event.questId,
                value = event.reward
            )
        )
    }
}
```

For more about each part, see [Creating a Trigger](creating-a-trigger).

## Step 3: Make the effect

```kotlin
package com.example.myplugin

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getIntFromExpression
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter

object EffectRestoreHunger : Effect<NoCompileData>("restore_hunger") {
    override val description = "Gives food points to the player."

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

For more about each part, see [Creating an Effect](creating-an-effect).

## Steps 4 and 5: Register, make the holder, and give it to players

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
import com.willfp.libreforge.triggers.Triggers
import org.bukkit.entity.Player

class MyPlugin : LibreforgePlugin() {
    // It is null until the first reload, and the provider must accept this.
    private var questHolder: Holder? = null

    override fun handleEnable() {
        // Step 4: Register the trigger and the effect.
        Triggers.register(TriggerCompleteQuest)
        Effects.register(EffectRestoreHunger)

        // Step 5: Give the holder to all players.
        registerSpecificHolderProvider<Player>(id = "myplugin:quests") { _ ->
            val holder = questHolder ?: return@registerSpecificHolderProvider emptyList()
            listOf(SimpleProvidedHolder(holder))
        }
    }

    override fun handleReload() {
        // Step 5: Make the holder from config.yml.
        val context = ViolationContext(this, "config.yml")

        questHolder = SimpleHolder(
            this.namespacedKeyFactory.create("quests"),
            Effects.compile(configYml.getSubsections("quests.effects"), context),
            Conditions.compile(configYml.getSubsections("quests.conditions"), context)
        )
    }
}
```

Note: A triggered effect also needs a holder. The trigger runs only the effects of the holders that the dispatcher has now.

## Step 6: Write the config

```yaml
# config.yml
quests:
  effects:
    - id: restore_hunger
      args:
        amount: "%value% / 10"
      triggers:
        - complete_quest
    - id: send_message
      args:
        message: "&aYou completed %text%!"
      triggers:
        - complete_quest
  conditions: []
```

`%value%` is the number from the trigger data. `%text%` is the text. Libreforge adds these placeholders for you.

## What occurs at runtime

1. Your plugin calls `QuestCompleteEvent` with a reward of `50`.
2. `complete_quest` sends the trigger data to libreforge with `dispatch`.
3. Libreforge finds the active effects of the player that use `complete_quest`.
4. Libreforge checks the mutators, the filters, and the conditions of each effect.
5. `restore_hunger` runs. `%value% / 10` gives `5`, so the player gets 5 food points.
6. `send_message` runs. The player sees "You completed slay_dragon!".
