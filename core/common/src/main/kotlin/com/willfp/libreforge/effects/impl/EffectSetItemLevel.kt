package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getFormattedString
import com.willfp.libreforge.getIntFromExpression
import com.willfp.libreforge.levels.LevelTypes
import com.willfp.libreforge.levels.levels
import com.willfp.libreforge.toPlaceholderContext
import com.willfp.libreforge.triggers.TriggerData

object EffectSetItemLevel : Effect<NoCompileData>("set_item_level") {
    override val description = "Sets the triggered item's level directly, rather than granting XP towards it."
    override val categories = setOf("inventory")

    override val isPermanent = false

    override val arguments = arguments {
        require("id", "You must specify a valid level ID!", Config::getString) {
            LevelTypes[it] != null
        }
        describe(
            "id",
            description = "The ID of the level type to set.",
            type = ArgType.STRING,
            example = "mining"
        )
        require(
            "level",
            "You must specify the level to set!",
            description = "The level to set the item to. Supports expressions.",
            type = ArgType.EXPRESSION,
            example = "%level% + 1"
        )
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val item = data.foundItem ?: return false
        val level = LevelTypes[config.getFormattedString("id", data)] ?: return false

        val newLevel = config.getIntFromExpression("level", data)
        item.levels.setLevel(level, newLevel, config.toPlaceholderContext(data))

        return true
    }
}
