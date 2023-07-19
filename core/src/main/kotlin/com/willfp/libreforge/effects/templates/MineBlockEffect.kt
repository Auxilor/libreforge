package com.willfp.libreforge.effects.templates

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.runExempted
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.plugin
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.Player

abstract class MineBlockEffect<T : Any>(id: String) : Effect<T>(id) {
    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override fun shouldTrigger(config: Config, data: TriggerData, compileData: T): Boolean {
        val block = data.block ?: data.location?.block ?: return false
        return !block.hasMetadata("block-ignore")
    }

    protected fun Player.breakBlocksSafely(blocks: Collection<Block>) {
        if (plugin.configYml.getBool("effects.use-setblock-break")) {
            blocks.forEach { it.type = Material.AIR }
        } else {
            this.runExempted {
                for (block in blocks) {
                    block.setMetadata("block-ignore", plugin.createMetadataValue(true))
                    this.breakBlock(block)
                    block.removeMetadata("block-ignore", plugin)
                }
            }
        }
    }
}
