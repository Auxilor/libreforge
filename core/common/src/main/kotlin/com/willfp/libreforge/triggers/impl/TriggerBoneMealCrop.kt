package com.willfp.libreforge.triggers.impl

import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockFertilizeEvent

object TriggerBoneMealCrop : Trigger("bonemeal_crop") {
    override val description = "Fires when the player uses bone meal to fertilize a crop."

    override val categories = setOf("world", "interaction")

    override val parameterDescriptions = mapOf(
        TriggerParameter.BLOCK to "The crop block that was fertilized.",
        TriggerParameter.LOCATION to "The location of the fertilized block."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.BLOCK,
        TriggerParameter.EVENT,
        TriggerParameter.LOCATION
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: BlockFertilizeEvent) {
        val player = event.player ?: return

        this.dispatch(
            player.toDispatcher(),
            TriggerData(
                player = player,
                location = player.location,
                block = event.block,
                event = event
            )
        )
    }
}
