package com.willfp.libreforge.triggers.impl

import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityPickupItemEvent

object TriggerPickUpItem : Trigger("pick_up_item") {
    override val description = "Fires when an entity picks up an item from the ground."

    override val categories = setOf("inventory")

    override val parameterDescriptions = mapOf(
        TriggerParameter.VICTIM to "The entity that picked up the item.",
        TriggerParameter.ITEM to "The item that was picked up.",
        TriggerParameter.VALUE to "The stack size of the picked-up item."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.VICTIM,
        TriggerParameter.ITEM,
        TriggerParameter.VALUE
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: EntityPickupItemEvent) {
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
