package com.willfp.libreforge.effects.templates

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.runExempted
import com.willfp.libreforge.*
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.plugin
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.NamespacedKey
import org.bukkit.block.Block
import org.bukkit.entity.Player

abstract class MineBlockEffect<T : Any>(id: String) : Effect<T>(id) {
    override val parameters = setOf(
        TriggerParameter.PLAYER
    )
    private val ignore = NamespacedKey(plugin, "block-ignore")

    override fun shouldTrigger(config: Config, data: TriggerData, compileData: T): Boolean {
        val block = data.block ?: data.location?.block ?: return false
        return block.getPDCNoSave()?.hasBool(ignore) != true
    }

    protected fun Player.breakBlocksSafely(blocks: Collection<Block>) {
        this.runExempted {
            for (block in blocks) {
                block.pdc.setBool(ignore, true)
                this.breakBlock(block)
                block.pdc.remove(ignore)
            }
        }
    }
}
