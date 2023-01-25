package com.willfp.libreforge.integrations.boosters

import com.willfp.boosters.boosters.Boosters
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import org.bukkit.entity.Player

class ConditionIsBoosterActive : Condition("is_booster_active") {
    override val arguments = arguments {
        require("booster", "You must specify the booster!")
    }

    override fun isConditionMet(player: Player, config: Config): Boolean {
        return Boosters.getByID(config.getString("booster"))?.active != null
    }
}
