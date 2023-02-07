package com.willfp.libreforge.filters.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.triggers.TriggerData
import org.bukkit.block.data.Ageable

object FilterFullyGrown : Filter<NoCompileData, Boolean>("fully_grown") {
    override fun getValue(config: Config, data: TriggerData?, key: String): Boolean {
        return config.getBool(key)
    }

    override fun filter(data: TriggerData, value: Boolean, compileData: NoCompileData): Boolean {
        val block = data.block ?: return true
        val blockData = block.blockData

        val isFullyGrown = if (blockData is Ageable) {
            blockData.age == blockData.maximumAge
        } else {
            true
        }

        return isFullyGrown == value
    }
}
