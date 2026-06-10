package com.willfp.libreforge.triggers.impl

import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot

object TriggerClickBlock : Trigger("click_block") {
    override val description = "Fires when the player clicks on a block."

    override val categories = setOf("interaction")

    override val parameterDescriptions = mapOf(
        TriggerParameter.BLOCK to "The block that was clicked.",
        TriggerParameter.LOCATION to "The location of the clicked block."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.BLOCK,
        TriggerParameter.EVENT,
        TriggerParameter.LOCATION
    )

    private val LEFT_CLICK_ITEMS = listOf(
        Material.FISHING_ROD,
        Material.BOW,
        Material.CROSSBOW,
        Material.TRIDENT
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: PlayerInteractEvent) {
        val block = event.clickedBlock ?: return
        val player = event.player

        if (event.hand != EquipmentSlot.HAND) {
            return
        }

        if (LEFT_CLICK_ITEMS.contains(event.item?.type)) {
            if (event.action != Action.LEFT_CLICK_BLOCK) {
                return
            }
        } else {
            if (event.action != Action.RIGHT_CLICK_BLOCK) {
                return
            }
        }

        this.dispatch(
            player.toDispatcher(),
            TriggerData(
                player = player,
                location = block.location,
                block = block,
                event = event
            )
        )
    }
}
