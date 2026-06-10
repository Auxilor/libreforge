package com.willfp.libreforge.triggers.impl

import com.willfp.libreforge.levels.event.ItemLevelUpEvent
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.EventHandler

object TriggerLevelUpItem : Trigger("level_up_item") {
    override val description = "Fires when a libreforge item levels up."

    override val categories = setOf("player")

    override val parameterDescriptions = mapOf(
        TriggerParameter.ITEM to "The item that levelled up.",
        TriggerParameter.TEXT to "The ID of the item that was levelled up.",
        TriggerParameter.VALUE to "The new level of the item."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.ITEM,
        TriggerParameter.TEXT,
        TriggerParameter.VALUE
    )

    @EventHandler
    fun handle(event: ItemLevelUpEvent) {
        this.dispatch(
            event.player.toDispatcher(),
            TriggerData(
                player = event.player,
                item = event.item,
                value = event.level.toDouble(),
                text = event.type.id
            )
        )
    }
}
