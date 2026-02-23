package com.willfp.libreforge.integrations.purpur.impl

import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.EventHandler
import org.purpurmc.purpur.event.inventory.GrindstoneTakeResultEvent

object TriggerGrindItem : Trigger("grind_item") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.ITEM,
        TriggerParameter.VALUE
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: GrindstoneTakeResultEvent) {
        val player = event.player

        this.dispatch(
            player.toDispatcher(),
            TriggerData(
                player = player,
                item = event.result,
                value = event.experienceAmount.toDouble()
            )
        )
    }
}
