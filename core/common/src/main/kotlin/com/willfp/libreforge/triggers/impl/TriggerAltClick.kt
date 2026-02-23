package com.willfp.libreforge.triggers.impl

import com.willfp.libreforge.plugin
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.FluidCollisionMode
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Tag
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import java.util.UUID

object TriggerAltClick : Trigger("alt_click") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.VICTIM,
        TriggerParameter.LOCATION,
        TriggerParameter.EVENT,
        TriggerParameter.ITEM,
        TriggerParameter.BLOCK
    )

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
        Material.DROPPER,
        Material.BEDROCK
    )

    private val preventDoubleTriggers = mutableSetOf<UUID>()

    init {
        BLOCK_BLACKLIST.addAll(Tag.BUTTONS.values)
        BLOCK_BLACKLIST.addAll(Tag.BEDS.values)
        BLOCK_BLACKLIST.addAll(Tag.DOORS.values)
        BLOCK_BLACKLIST.addAll(Tag.FENCE_GATES.values)
        BLOCK_BLACKLIST.addAll(Tag.TRAPDOORS.values)
        BLOCK_BLACKLIST.addAll(Tag.ANVIL.values)
        BLOCK_BLACKLIST.addAll(Tag.SHULKER_BOXES.values)
    }

    @EventHandler(priority = EventPriority.LOW)
    fun handle(event: PlayerInteractEvent) {
        val player = event.player

        if (player.uniqueId in preventDoubleTriggers) {
            return
        }

        val itemStack = player.inventory.itemInMainHand

        if (event.action == Action.PHYSICAL) {
            return
        }

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

        val location: Location?
        val world = player.location.world ?: return
        val result = player.rayTraceBlocks(
            plugin.configYml.getDouble("raytrace-distance"),
            FluidCollisionMode.NEVER
        )

        val entityResult = world.rayTraceEntities(
            player.eyeLocation,
            player.eyeLocation.direction, 50.0, 3.0
        ) { entity: Entity? -> entity is LivingEntity }

        location = result?.hitPosition?.toLocation(world)
            ?: if (entityResult != null) {
                entityResult.hitPosition.toLocation(world)
            } else {
                val dir = player.location.direction.normalize()
                    .multiply(plugin.configYml.getDoubleFromExpression("raytrace-distance"))
                player.location.add(dir)
            }

        val victim = entityResult?.hitEntity as? LivingEntity

        preventDoubleTriggers += player.uniqueId

        plugin.scheduler.run {
            preventDoubleTriggers -= player.uniqueId
        }

        this.dispatch(
            player.toDispatcher(),
            TriggerData(
                player = player,
                victim = victim,
                location = location,
                event = event,
                item = player.inventory.itemInMainHand,
                block = event.clickedBlock ?: result?.hitBlock ?: victim?.location?.block
            )
        )
    }
}
