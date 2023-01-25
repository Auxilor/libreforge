package com.willfp.libreforge.integrations.reforges

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.containsIgnoreCase
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import com.willfp.reforges.util.ReforgeLookup
import org.bukkit.entity.Player

class ConditionHasReforge : Condition("has_reforge") {
    override val arguments = arguments {
        require("reforge", "You must specify the reforge!")
    }

    override fun isConditionMet(player: Player, config: Config): Boolean {
        return ReforgeLookup.provideReforges(player).map { it.id }.containsIgnoreCase(config.getString("reforge"))
    }
}
