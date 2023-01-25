package com.willfp.libreforge.integrations.talismans

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.containsIgnoreCase
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import com.willfp.talismans.talismans.util.TalismanChecks
import org.bukkit.entity.Player

class ConditionHasTalisman : Condition("has_talisman") {
    override val arguments = arguments {
        require("talisman", "You must specify the talisman!")
    }

    override fun isConditionMet(player: Player, config: Config): Boolean {
        return TalismanChecks.getTalismansOnPlayer(player).map { it.id }.containsIgnoreCase(config.getString("talisman"))
    }
}
