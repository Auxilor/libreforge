package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.levels.LevelTypes
import com.willfp.libreforge.levels.levels
import com.willfp.libreforge.toPlaceholderContext
import com.willfp.libreforge.triggers.TriggerData

object EffectGiveItemLevel : Effect<NoCompileData>("give_item_level") {
    override val description = "Grants levels to the triggered item's level system, bypassing XP."
    override val categories = setOf("inventory")

    override val isPermanent = false

    override val arguments = arguments {
        require("id", "You must specify a valid level ID!", Config::getString) {
            LevelTypes[it] != null
        }
        describe(
            "id",
            description = "The ID of the level type to grant levels for.",
            type = ArgType.STRING
        )
        require(
            "levels",
            "You must specify the amount of levels to give!",
            description = "The amount of levels to grant to the item. Supports expressions. Negative values remove levels.",
            type = ArgType.EXPRESSION
        )
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val item = data.foundItem ?: return false
        val level = LevelTypes[config.getString("id")] ?: return false

        val levels = config.getDoubleFromExpression("levels", data).toInt()
        item.levels.gainLevels(level, levels, config.toPlaceholderContext(data))

        return true
    }
}
