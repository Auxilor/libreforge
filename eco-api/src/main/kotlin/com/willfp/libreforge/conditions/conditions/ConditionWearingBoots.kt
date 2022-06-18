package com.willfp.libreforge.conditions.conditions

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.items.Items
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.conditions.Condition
import org.bukkit.entity.Player

class ConditionWearingBoots : Condition("wearing_boots") {
    override fun isConditionMet(player: Player, config: Config): Boolean {
        return config.getStrings("items").map { Items.lookup(it) }.any {
            it.matches(player.inventory.boots)
        }
    }

    override fun validateConfig(config: Config): List<ConfigViolation> {
        val violations = mutableListOf<ConfigViolation>()

        if (!config.has("items")) violations.add(
            ConfigViolation(
                "items",
                "You must specify the list of items!"
            )
        )

        return violations
    }
}