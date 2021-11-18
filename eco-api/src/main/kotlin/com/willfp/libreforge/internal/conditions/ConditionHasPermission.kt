package com.willfp.libreforge.internal.conditions

import com.willfp.eco.core.config.interfaces.JSONConfig
import com.willfp.libreforge.api.conditions.Condition
import org.bukkit.entity.Player


class ConditionHasPermission : Condition("is_sneaking") {
    override fun isConditionMet(player: Player, config: JSONConfig): Boolean {
        return player.hasPermission(config.getString("permission"))
    }
}