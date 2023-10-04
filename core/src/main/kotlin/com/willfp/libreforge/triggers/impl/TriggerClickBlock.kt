package com.willfp.libreforge.triggers.impl

import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.Material
import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent

object TriggerClickBlock : Trigger("click_block") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION,
        TriggerParameter.BLOCK,
        TriggerParameter.EVENT
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

        if (EquipmentSlot.HAND != event.hand) {
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
            player,
            TriggerData(
                player = player,
                location = block.location,
                block = block,
                event = event
            )
        )
    }
}
