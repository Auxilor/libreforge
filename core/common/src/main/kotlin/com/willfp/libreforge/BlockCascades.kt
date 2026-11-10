package com.willfp.libreforge

import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockFace

/**
 * Blocks that break along with a broken block, such as the rest of a sugar
 * cane, bamboo, kelp, vine or chorus plant.
 */
object BlockCascades {
    private sealed interface CascadeStrategy {
        val direction: BlockFace

        fun collect(source: Block): List<Block>
    }

    private class ColumnCascade(
        private val materials: Set<Material>,
        override val direction: BlockFace
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
        override val direction = BlockFace.UP

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

        val caveVines = ColumnCascade(
            setOf(Material.CAVE_VINES, Material.CAVE_VINES_PLANT),
            BlockFace.DOWN
        )
        put(Material.CAVE_VINES, caveVines)
        put(Material.CAVE_VINES_PLANT, caveVines)

        val chorus = ConnectedCascade(
            setOf(Material.CHORUS_PLANT, Material.CHORUS_FLOWER)
        )
        put(Material.CHORUS_PLANT, chorus)
        put(Material.CHORUS_FLOWER, chorus)
    }

    /**
     * The cascade started by breaking [source], [source] first, or an empty
     * list when [source] is not a cascading block.
     */
    fun collect(source: Block): List<Block> =
        strategyByMaterial[source.type]?.collect(source).orEmpty()

    /**
     * The blocks that break because [broken] broke, without [broken] itself:
     * the rest of its cascade, or the plant resting on or hanging from it.
     */
    fun dependents(broken: Block): List<Block> {
        if (broken.type in strategyByMaterial) {
            return collect(broken).drop(1)
        }

        return listOf(BlockFace.UP, BlockFace.DOWN).flatMap { face ->
            val neighbour = broken.getRelative(face)
            strategyByMaterial[neighbour.type]
                ?.takeIf { it.direction == face }
                ?.collect(neighbour)
                .orEmpty()
        }
    }
}
