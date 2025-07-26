package com.willfp.libreforge.integrations.edprisoncore.impl

import com.edwardbelt.edprison.utils.LevelUtils
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.get
import org.bukkit.entity.Player

object ConditionHasEdPrisonLevel : Condition<NoCompileData>("has_edprison_level") {
    override val arguments = arguments {
        require("type", "You must specify the level type!")
        require("level", "You must specify the level!")
    }

    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ): Boolean {
        val player = dispatcher.get<Player>()?.uniqueId ?: return false
        val level = config.getInt("level")
        val currency = config.getString("type")

        return LevelUtils.getLevel(player, currency) >= level
    }
}