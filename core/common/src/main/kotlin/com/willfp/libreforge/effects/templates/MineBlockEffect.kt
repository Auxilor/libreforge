package com.willfp.libreforge.effects.templates

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.plugin
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.Player

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
        if (plugin.configYml.getBool("effects.use-setblock-break")) {
            blocks.forEach { it.type = Material.AIR }
        } else {
            for (block in blocks) {
                if (block.world != this.world) {
                    continue
                }

                block.setMetadata(ignoreKey, plugin.createMetadataValue(true))
                this.breakBlock(block)
                block.removeMetadata(ignoreKey, plugin)
            }
        }
    }
}