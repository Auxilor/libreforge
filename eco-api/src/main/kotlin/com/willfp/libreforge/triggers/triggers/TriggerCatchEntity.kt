package com.willfp.libreforge.triggers.triggers

import com.willfp.eco.core.integrations.mcmmo.McmmoManager
import com.willfp.libreforge.triggers.GenericCancellableEvent
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.entity.Item
import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerFishEvent

class TriggerCatchEntity : Trigger(
    "catch_entity", listOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION,
        TriggerParameter.VICTIM,
        TriggerParameter.EVENT,
        TriggerParameter.ITEM
    )
) {
    @EventHandler(ignoreCancelled = true)
    fun handle(event: PlayerFishEvent) {
        if (McmmoManager.isFake(event)) {
            return
        }

        if (event.state != PlayerFishEvent.State.CAUGHT_ENTITY) {
            return
        }

        val player = event.player

        this.processTrigger(
            player,
            TriggerData(
                player = player,
                location = event.caught?.location ?: player.location,
                victim = event.caught as? LivingEntity,
                event = GenericCancellableEvent(event),
                item = (event.caught as? Item)?.itemStack
            )
        )
    }
}
