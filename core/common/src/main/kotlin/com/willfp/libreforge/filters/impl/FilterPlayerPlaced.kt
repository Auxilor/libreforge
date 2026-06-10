package com.willfp.libreforge.filters.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.isPlayerPlaced
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.triggers.TriggerData

object FilterPlayerPlaced : Filter<NoCompileData, Boolean>("player_placed") {
    override val description = "Matches when the block was (or was not) placed by a player."
    override val categories = setOf("world")
    override val valueType = ArgType.BOOLEAN
    override val additionalInfo = listOf("Passes automatically when no block is present in the trigger data.")

    override fun getValue(config: Config, data: TriggerData?, key: String): Boolean {
        return config.getBool(key)
    }

    override fun isMet(data: TriggerData, value: Boolean, compileData: NoCompileData): Boolean {
        val block = data.block ?: return true

        return block.isPlayerPlaced == value
    }
}
