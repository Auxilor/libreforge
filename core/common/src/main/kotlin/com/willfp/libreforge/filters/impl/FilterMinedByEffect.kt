package com.willfp.libreforge.filters.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.effects.templates.MineBlockEffect
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.triggers.TriggerData

object FilterMinedByEffect : Filter<NoCompileData, Boolean>("mined_by_effect") {
    override val description = "Matches when the block was (or was not) broken by a mining effect, such as mine_vein or mine_radius, rather than by the player directly."
    override val categories = setOf("world")
    override val valueType = ArgType.BOOLEAN
    override val additionalInfo = listOf(
        "Use mined_by_effect: false to run an effect once per mine, for the block the player broke, instead of once for every extra block.",
        "Passes automatically when no block is present in the trigger data."
    )

    override fun getValue(config: Config, data: TriggerData?, key: String) = config.getBool(key)

    override fun isMet(data: TriggerData, value: Boolean, compileData: NoCompileData): Boolean {
        val block = data.block ?: return true

        return block.hasMetadata(MineBlockEffect.ignoreKey) == value
    }
}
