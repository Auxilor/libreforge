package com.willfp.libreforge.filters.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.triggers.TriggerData
import org.bukkit.Material
import org.bukkit.block.data.type.Beehive

object FilterHoneyLevelFull : Filter<NoCompileData, Boolean>("honey_level_full") {
    override fun getValue(config: Config, data: TriggerData?, key: String): Boolean {
        return true
    }

    override fun isMet(data: TriggerData, value: Boolean, compileData: NoCompileData): Boolean {
        val block = data.block ?: return false

        if (block.type == Material.BEEHIVE || block.type == Material.BEE_NEST) {
            val beehiveData = block.blockData as? Beehive ?: return false
            return beehiveData.honeyLevel == 5
        }

        return false
    }
}
