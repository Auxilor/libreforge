package com.willfp.libreforge.filters.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.triggers.TriggerData
import org.bukkit.block.data.Ageable
import org.bukkit.block.data.type.CaveVinesPlant

object FilterFullyGrown : Filter<NoCompileData, Boolean>("fully_grown") {
    override fun getValue(config: Config, data: TriggerData?, key: String): Boolean {
        return config.getBool(key)
    }

    override fun isMet(data: TriggerData, value: Boolean, compileData: NoCompileData): Boolean {
        val block = data.block ?: return true
        val isFullyGrown = when(val blockData = block.blockData) {
            is Ageable -> {
                blockData.age == blockData.maximumAge
            }
            is CaveVinesPlant -> {
                blockData.isBerries
            }
            else -> {
                true
            }
        }

        return isFullyGrown == value
    }
}
