package com.willfp.libreforge.triggers.impl

import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.entity.Item
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerFishEvent

object TriggerCatchFishFail : Trigger("catch_fish_fail") {
    override val description = "Fires when the player fails a fishing attempt."

    override val categories = setOf("fishing")

    override val parameterDescriptions = mapOf(
        TriggerParameter.LOCATION to "The location of the fishing hook.",
        TriggerParameter.ITEM to "The item on the hook, if any."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.EVENT,
        TriggerParameter.LOCATION,
        TriggerParameter.ITEM
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: PlayerFishEvent) {
        if (event.state != PlayerFishEvent.State.FAILED_ATTEMPT) {
            return
        }

        val player = event.player

        this.dispatch(
            player.toDispatcher(),
            TriggerData(
                player = player,
                location = event.hook.location,
                event = event,
                item = (event.caught as? Item)?.itemStack
            )
        )
    }
}
