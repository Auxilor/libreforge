package com.willfp.libreforge.integrations.xiaomomiplugins.customfishing.impl

import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import net.momirealms.customfishing.api.event.FishingLootSpawnEvent
import org.bukkit.entity.Item
import org.bukkit.event.EventHandler

object TriggerCatchFish : Trigger("catch_fish") {
    override val description = "Fires when the player catches a fish through CustomFishing."

    override val categories = setOf("fishing")

    override val additionalInfo = listOf("Requires CustomFishing (xiaomomi) to be installed.")

    override val parameterDescriptions = mapOf(
        TriggerParameter.LOCATION to "The location of the catch.",
        TriggerParameter.TEXT to "The loot ID of the caught item.",
        TriggerParameter.ITEM to "The item that was caught, if any."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.EVENT,
        TriggerParameter.LOCATION,
        TriggerParameter.TEXT,
        TriggerParameter.ITEM
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: FishingLootSpawnEvent) {
        val player = event.player
        val loot = event.loot.id() ?: return
        val location = event.location ?: return
        val entity = event.entity
        val itemStack = (entity as? Item)?.itemStack

        this.dispatch(
            player.toDispatcher(),
            TriggerData(
                player = player,
                event = event,
                location = location,
                text = loot,
                item = itemStack
            )
        )
    }
}