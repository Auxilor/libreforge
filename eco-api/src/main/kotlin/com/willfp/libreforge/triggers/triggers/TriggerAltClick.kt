package com.willfp.libreforge.triggers.triggers

import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import org.bukkit.FluidCollisionMode
import org.bukkit.Material
import org.bukkit.Tag
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent

class TriggerAltClick : Trigger("alt_click") {
    companion object {
        private val LEFT_CLICK_ITEMS = listOf(
            Material.FISHING_ROD,
            Material.BOW,
            Material.CROSSBOW,
            Material.TRIDENT
        )

        private val BLOCK_BLACKLIST = mutableListOf(
            Material.CRAFTING_TABLE,
            Material.GRINDSTONE,
            Material.ENCHANTING_TABLE,
            Material.FURNACE,
            Material.SMITHING_TABLE,
            Material.LEVER,
            Material.REPEATER,
            Material.COMPARATOR,
            Material.RESPAWN_ANCHOR,
            Material.NOTE_BLOCK,
            Material.ITEM_FRAME,
            Material.CHEST,
            Material.BARREL,
            Material.BEACON,
            Material.LECTERN,
            Material.FLETCHING_TABLE,
            Material.SMITHING_TABLE,
            Material.STONECUTTER,
            Material.SMOKER,
            Material.BLAST_FURNACE,
            Material.BREWING_STAND,
            Material.DISPENSER,
            Material.DROPPER
        )

        init {
            BLOCK_BLACKLIST.addAll(Tag.BUTTONS.values)
            BLOCK_BLACKLIST.addAll(Tag.BEDS.values)
            BLOCK_BLACKLIST.addAll(Tag.DOORS.values)
            BLOCK_BLACKLIST.addAll(Tag.FENCE_GATES.values)
            BLOCK_BLACKLIST.addAll(Tag.TRAPDOORS.values)
            BLOCK_BLACKLIST.addAll(Tag.ANVIL.values)
            BLOCK_BLACKLIST.addAll(Tag.SHULKER_BOXES.values)
        }
    }

    @EventHandler
    fun altClickListener(event: PlayerInteractEvent) {
        val player = event.player
        val itemStack = player.inventory.itemInMainHand

        if (LEFT_CLICK_ITEMS.contains(itemStack.type)) {
            if (event.action == Action.RIGHT_CLICK_AIR || event.action == Action.RIGHT_CLICK_BLOCK) {
                return
            }
        } else {
            if (event.action == Action.LEFT_CLICK_AIR || event.action == Action.LEFT_CLICK_BLOCK) {
                return
            }
        }

        if (event.clickedBlock != null) {
            if (BLOCK_BLACKLIST.contains(event.clickedBlock!!.type)) {
                return
            }
        }

        val result = player.rayTraceBlocks(50.0, FluidCollisionMode.NEVER) ?: return
        val world = player.location.world ?: return

        val entityResult = world.rayTraceEntities(
            player.eyeLocation,
            player.eyeLocation.direction, 50.0, 3.0
        ) { entity: Entity? -> entity is LivingEntity }

        this.processTrigger(
            player,
            TriggerData(
                player = player,
                victim = entityResult?.hitEntity as? LivingEntity,
                location = result.hitPosition.toLocation(world)
            )
        )
    }
}
