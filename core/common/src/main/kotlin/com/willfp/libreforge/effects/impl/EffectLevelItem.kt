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

object EffectLevelItem : Effect<NoCompileData>("level_item") {
    override val description = "Grants XP to the triggered item's level system."
    override val categories = setOf("inventory")

    override val isPermanent = false

    override val arguments = arguments {
        require("id", "You must specify a valid level ID!", Config::getString) {
            LevelTypes[it] != null
        }
        describe(
            "id",
            description = "The ID of the level type to grant XP for.",
            type = ArgType.STRING,
            example = "mining"
        )
        require(
            "xp",
            "You must specify the amount of xp to give!",
            description = "The amount of XP to grant to the item. Supports expressions.",
            type = ArgType.EXPRESSION,
            example = "%level% * 10"
        )
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val item = data.foundItem ?: return false
        val level = LevelTypes[config.getString("id")] ?: return false

        val xp = config.getDoubleFromExpression("xp", data)
        item.levels.gainXP(level, xp, config.toPlaceholderContext(data))

        return true
    }
}
