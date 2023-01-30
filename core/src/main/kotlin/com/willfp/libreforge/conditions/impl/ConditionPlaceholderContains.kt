package com.willfp.libreforge.conditions.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.integrations.placeholder.PlaceholderManager
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import org.bukkit.entity.Player

object ConditionPlaceholderContains : Condition<NoCompileData>("placeholder_contains") {
    override val arguments = arguments {
        require("placeholder", "You must specify the placeholder!")
    }

    override fun isMet(player: Player, config: Config, compileData: NoCompileData): Boolean {
        return PlaceholderManager.translatePlaceholders(config.getString("placeholder"), player)
            .contains(config.getString("value"), ignoreCase = config.getBool("ignore_case"))
    }
}
