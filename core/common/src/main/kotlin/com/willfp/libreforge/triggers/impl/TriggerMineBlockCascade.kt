package com.willfp.libreforge.triggers.impl

import com.willfp.eco.core.integrations.antigrief.AntigriefManager
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.block.BlockBreakEvent

object TriggerMineBlockCascade : Trigger("mine_block_cascade") {
    override val description = "Fires for each block broken in a cascading plant break, such as sugar cane, bamboo, kelp, or chorus."

    override val categories = setOf("world")

    override val parameterDescriptions = mapOf(
        TriggerParameter.BLOCK to "The block in the cascade that was broken.",
        TriggerParameter.LOCATION to "The location of the block.",
        TriggerParameter.ITEM to "The item in the player's main hand."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.BLOCK,
        TriggerParameter.EVENT,
        TriggerParameter.LOCATION,
        TriggerParameter.ITEM
    )

    private sealed interface CascadeStrategy {
        fun collect(source: Block): List<Block>
    }

    private class ColumnCascade(
        private val materials: Set<Material>,
        private val direction: BlockFace
    ) : CascadeStrategy {
        override fun collect(source: Block): List<Block> {
            val out = mutableListOf(source)
            var current = source.getRelative(direction)
            while (current.type in materials) {
                out.add(current)
                current = current.getRelative(direction)
            }
            return out
        }
    }

    private class ConnectedCascade(
        private val materials: Set<Material>
    ) : CascadeStrategy {
        private val faces = arrayOf(
            BlockFace.UP, BlockFace.DOWN,
            BlockFace.NORTH, BlockFace.SOUTH,
            BlockFace.EAST, BlockFace.WEST
        )

        override fun collect(source: Block): List<Block> {
            val out = mutableListOf<Block>()
            val visited = HashSet<Block>()
            val queue = ArrayDeque<Block>()
            queue.add(source)
            visited.add(source)
            while (queue.isNotEmpty()) {
                val block = queue.removeFirst()
                out.add(block)
                for (face in faces) {
                    val neighbour = block.getRelative(face)
                    if (neighbour.type in materials && visited.add(neighbour)) {
                        queue.add(neighbour)
                    }
                }
            }
            return out
        }
    }

    private val strategyByMaterial: Map<Material, CascadeStrategy> = buildMap {
        val sugarCane = ColumnCascade(setOf(Material.SUGAR_CANE), BlockFace.UP)
        put(Material.SUGAR_CANE, sugarCane)

        val bamboo = ColumnCascade(
            setOf(Material.BAMBOO, Material.BAMBOO_SAPLING),
            BlockFace.UP
        )
        put(Material.BAMBOO, bamboo)
        put(Material.BAMBOO_SAPLING, bamboo)

        val cactus = ColumnCascade(setOf(Material.CACTUS), BlockFace.UP)
        put(Material.CACTUS, cactus)

        val kelp = ColumnCascade(
            setOf(Material.KELP, Material.KELP_PLANT),
            BlockFace.UP
        )
        put(Material.KELP, kelp)
        put(Material.KELP_PLANT, kelp)

        val twistingVines = ColumnCascade(
            setOf(Material.TWISTING_VINES, Material.TWISTING_VINES_PLANT),
            BlockFace.UP
        )
        put(Material.TWISTING_VINES, twistingVines)
        put(Material.TWISTING_VINES_PLANT, twistingVines)

        val weepingVines = ColumnCascade(
            setOf(Material.WEEPING_VINES, Material.WEEPING_VINES_PLANT),
            BlockFace.DOWN
        )
        put(Material.WEEPING_VINES, weepingVines)
        put(Material.WEEPING_VINES_PLANT, weepingVines)

        val chorus = ConnectedCascade(
            setOf(Material.CHORUS_PLANT, Material.CHORUS_FLOWER)
        )
        put(Material.CHORUS_PLANT, chorus)
        put(Material.CHORUS_FLOWER, chorus)
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun handle(event: BlockBreakEvent) {
        val player = event.player
        val source = event.block

        val strategy = strategyByMaterial[source.type] ?: return

        if (!AntigriefManager.canBreakBlock(player, source)) {
            return
        }

        val item = player.inventory.itemInMainHand
        val dispatcher = player.toDispatcher()

        for (block in strategy.collect(source)) {
            this.dispatch(
                dispatcher,
                TriggerData(
                    player = player,
                    block = block,
                    location = block.location,
                    event = event,
                    item = item
                )
            )
        }
    }
}
