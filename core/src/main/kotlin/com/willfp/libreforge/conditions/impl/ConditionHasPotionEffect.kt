package com.willfp.libreforge.conditions.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.containsIgnoreCase
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import org.bukkit.entity.Player

class ConditionHasPotionEffect : Condition("has_potion_effect") {
    override val arguments = arguments {
        require("effect", "You must specify the potion effect!")
    }

    override fun isConditionMet(player: Player, config: Config): Boolean {
        return player.activePotionEffects.map { it.type.name }.containsIgnoreCase(
            config.getString("effect")
        )
    }
}
