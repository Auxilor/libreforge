package com.willfp.libreforge.triggers.impl

import com.willfp.libreforge.PlayerDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerItemDamageEvent

object TriggerDamageItem : Trigger("damage_item") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION,
        TriggerParameter.ITEM,
        TriggerParameter.EVENT,
        TriggerParameter.VALUE
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: PlayerItemDamageEvent) {
        val player = event.player

        this.dispatch(
            PlayerDispatcher(player),
            TriggerData(
                player = player,
                location = player.location,
                item = event.item,
                value = event.damage.toDouble(),
                event = event
            )
        )
    }
}
