package com.willfp.libreforge.conditions.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.Dispatcher
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
    override val arguments = arguments {
        require("id", "You must specify the level ID to check for!")
        require("level", "You must specify the maximum level!")
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
