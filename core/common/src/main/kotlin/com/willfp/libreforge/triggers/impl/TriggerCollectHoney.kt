package com.willfp.libreforge.triggers.impl

import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.Material
import org.bukkit.block.data.type.Beehive
import org.bukkit.event.EventHandler
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot

object TriggerCollectHoney : Trigger("collect_honey") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION,
        TriggerParameter.BLOCK,
        TriggerParameter.EVENT
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: PlayerInteractEvent) {
        val block = event.clickedBlock ?: return
        val player = event.player

        if (event.action != Action.RIGHT_CLICK_BLOCK) {
            return
        }

        if (block.type != Material.BEEHIVE && block.type != Material.BEE_NEST) {
            return
        }

        val beehive = block.blockData as? Beehive ?: return
        if (beehive.honeyLevel != 5) {
            return
        }

        val mainHandItem = player.inventory.itemInMainHand
        val offHandItem = player.inventory.itemInOffHand
        val isBottleInMainHand = mainHandItem.type == Material.GLASS_BOTTLE
        val isBottleInOffHand = offHandItem.type == Material.GLASS_BOTTLE

        if (!isBottleInMainHand && !isBottleInOffHand) {
            return
        }

        if (event.hand == EquipmentSlot.HAND && isBottleInMainHand) {
            triggerEvent(player, block, event)
        } else if (event.hand == EquipmentSlot.OFF_HAND && isBottleInOffHand) {
            triggerEvent(player, block, event)
        }
    }

    private fun triggerEvent(player: org.bukkit.entity.Player, block: org.bukkit.block.Block, event: PlayerInteractEvent) {
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
