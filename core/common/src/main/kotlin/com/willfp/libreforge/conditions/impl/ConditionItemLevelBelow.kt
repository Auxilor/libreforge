package com.willfp.libreforge.conditions.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.get
import com.willfp.libreforge.getProvider
import com.willfp.libreforge.levels.LevelTypes
import com.willfp.libreforge.levels.levels
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object ConditionItemLevelBelow : Condition<NoCompileData>("item_level_below") {
    override val description = "Passes when the held item's level for a given level type is below the specified value."
    override val categories = setOf("inventory")

    override val arguments = arguments {
        require(
            "id",
            "You must specify the level ID to check for!",
            description = "The ID of the level type to check on the held item.",
            type = ArgType.STRING
        )
        require(
            "level",
            "You must specify the maximum level!",
            description = "The maximum level the item must be below.",
            type = ArgType.EXPRESSION,
            example = "5 + %level%"
        )
    }

    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ): Boolean {
        val item = holder.getProvider<ItemStack>() ?: return false
        val type = LevelTypes[config.getString("id")]
        val level = config.getIntFromExpression("level", dispatcher.get<Player>())

        return item.levels[type].level < level
    }
}
