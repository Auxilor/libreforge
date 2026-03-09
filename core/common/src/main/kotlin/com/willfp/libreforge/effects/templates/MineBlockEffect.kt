package com.willfp.libreforge.effects.templates

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.events.MultiBlockBreakEvent
import com.willfp.eco.core.events.MultiBlockDropItemEvent
import com.willfp.eco.util.runExempted
import com.willfp.libreforge.applyDamage
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.plugin
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.Item
import org.bukkit.entity.Player
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockDropItemEvent

abstract class MineBlockEffect<T : Any>(id: String) : Effect<T>(id) {
    private val ignoreKey = "blockbreakevent-ignore"

    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override fun shouldTrigger(config: Config, data: TriggerData, compileData: T): Boolean {
        val block = data.block ?: data.location?.block ?: return false
        return !block.hasMetadata(ignoreKey)
    }

    protected fun Player.breakBlocksSafely(blocks: Collection<Block>) {
        val item = this.inventory.itemInMainHand
        val useMultiBlocksEvents = plugin.configYml.getBool("effects.use-multiblock-events")

        if (plugin.configYml.getBool("effects.use-setblock-break")) {
            blocks.forEach { it.type = Material.AIR }
        } else {
            this.runExempted {
                val blockList = mutableMapOf<Block, MultiBlockDropItemEvent.BlockStateAndItems>()

                for (block in blocks) {
                    if (block.world != this.world) {
                        continue
                    }

                    var items = block.getDrops(item).map {
                        block.world.createEntity(
                            block.location.toCenterLocation(),
                            Item::class.java
                        ).apply { itemStack = it }
                    }

                    if (!useMultiBlocksEvents) {
                        val blockBreak = BlockBreakEvent(block, this)
                        Bukkit.getPluginManager().callEvent(blockBreak)
                        if (blockBreak.isCancelled)
                            continue

                        if (blockBreak.isDropItems) {
                            val blockDrop = BlockDropItemEvent(block, block.state, this, items)

                            Bukkit.getPluginManager().callEvent(blockDrop)
                            if (blockDrop.isCancelled)
                                continue

                            items = blockDrop.items
                        }
                    }

                    blockList[block] = MultiBlockDropItemEvent.BlockStateAndItems(block.state, items)
                }

                val multiBlockBreak = MultiBlockBreakEvent(this, blockList.keys)

                if (useMultiBlocksEvents) {
                    Bukkit.getPluginManager().callEvent(multiBlockBreak)
                    if (multiBlockBreak.isCancelled)
                        return@runExempted
                }

                // blockList is probably mutated by the event above, so we put it after
                val multiBlockDrop = MultiBlockDropItemEvent(this, blockList)

                if (useMultiBlocksEvents) {
                    Bukkit.getPluginManager().callEvent(multiBlockDrop)
                    if (multiBlockDrop.isCancelled)
                        return@runExempted
                }

                val damageToApply = blockList.size

                val iter = blockList.iterator()
                while (iter.hasNext()) {
                    val (block, entry) = iter.next()
                    block.setMetadata(ignoreKey, plugin.createMetadataValue(true))
                    block.type = Material.AIR
                    if (multiBlockBreak.isDropItems(block))
                        entry.items.forEach { it.spawnAt(block.location.toCenterLocation()) }
                    block.removeMetadata(ignoreKey, plugin)
                    iter.remove()
                }

                item.applyDamage(damageToApply, this) {
                    this.inventory.setItemInMainHand(item.withType(Material.AIR))
                }
            }
        }
    }
}
