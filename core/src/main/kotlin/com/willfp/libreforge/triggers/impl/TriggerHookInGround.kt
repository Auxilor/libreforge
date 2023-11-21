package com.willfp.libreforge.triggers.impl

import com.willfp.libreforge.EntityDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.entity.Item
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerFishEvent

object TriggerHookInGround : Trigger("hook_in_ground") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION,
        TriggerParameter.EVENT,
        TriggerParameter.ITEM
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: PlayerFishEvent) {
        if (event.state != PlayerFishEvent.State.IN_GROUND) {
            return
        }

        val player = event.player

        this.dispatch(
            EntityDispatcher(player),
            TriggerData(
                player = player,
                location = event.hook.location,
                event = event,
                item = (event.caught as? Item)?.itemStack
            )
        )
    }
}
