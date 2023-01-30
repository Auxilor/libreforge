package com.willfp.libreforge.conditions.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import org.bukkit.entity.Player


class ConditionHasPermission : Condition("has_permission") {
    override val arguments = arguments {
        require("permission", "You must specify the permission!")
    }

    override fun isConditionMet(player: Player, config: Config): Boolean {
        return player.hasPermission(config.getString("permission"))
    }
}
