package com.willfp.libreforge.triggers.impl

import com.willfp.eco.core.integrations.antigrief.AntigriefManager
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.event.EditableXpDropEvent
import com.willfp.libreforge.triggers.event.XpDropCause
import com.willfp.libreforge.triggers.event.XpDropContext
import org.bukkit.GameMode
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.block.BlockBreakEvent

object TriggerBlockXpDrop : Trigger("block_xp_drop") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.BLOCK,
        TriggerParameter.EVENT,
        TriggerParameter.LOCATION
    )

    @EventHandler(
        ignoreCancelled = true,
        priority = EventPriority.LOW
    )
    fun handle(event: BlockBreakEvent) {
        val player = event.player
        val block = event.block

        if (player.gameMode == GameMode.CREATIVE || player.gameMode == GameMode.SPECTATOR) {
            return
        }

        if (!AntigriefManager.canBreakBlock(player, block)) {
            return
        }

        val editableEvent = EditableXpDropEvent(
            initialXp = event.expToDrop,
            cause = XpDropCause.BLOCK,
            context = XpDropContext(
                player = player,
                block = block
            ),
            dropLocation = block.location
        )

        this.dispatch(
            player.toDispatcher(),
            TriggerData(
                player = player,
                block = block,
                location = block.location,
                event = editableEvent,
                value = event.expToDrop.toDouble()
            )
        )

        event.expToDrop = editableEvent.finalXp
    }
}
