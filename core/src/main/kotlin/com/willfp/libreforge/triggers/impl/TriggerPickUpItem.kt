package com.willfp.libreforge.triggers.impl

import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityPickupItemEvent

object TriggerPickUpItem : Trigger("pick_up_item") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.VICTIM,
        TriggerParameter.ITEM,
        TriggerParameter.VALUE
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: EntityPickupItemEvent) {
        if (!isEnabled) return
        val entity = event.entity

        this.dispatch(
            entity.toDispatcher(),
            TriggerData(
                player = entity as? Player,
                victim = entity,
                item = event.item.itemStack,
                value = event.item.itemStack.amount.toDouble()
            )
        )
    }
}
