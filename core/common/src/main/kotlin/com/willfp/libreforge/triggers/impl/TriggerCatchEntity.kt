package com.willfp.libreforge.triggers.impl

import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.entity.Item
import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerFishEvent

object TriggerCatchEntity : Trigger("catch_entity") {
    override val description = "Fires when the player catches a living entity with a fishing rod."

    override val categories = setOf("fishing")

    override val parameterDescriptions = mapOf(
        TriggerParameter.VICTIM to "The entity that was caught by the rod.",
        TriggerParameter.LOCATION to "The location of the caught entity.",
        TriggerParameter.ITEM to "The item entity on the hook, if any."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.VICTIM,
        TriggerParameter.EVENT,
        TriggerParameter.LOCATION,
        TriggerParameter.ITEM
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: PlayerFishEvent) {
        if (event.state != PlayerFishEvent.State.CAUGHT_ENTITY) {
            return
        }

        val player = event.player

        this.dispatch(
            player.toDispatcher(),
            TriggerData(
                player = player,
                location = event.caught?.location ?: player.location,
                victim = event.caught as? LivingEntity,
                event = event,
                item = (event.caught as? Item)?.itemStack
            )
        )
    }
}
