package com.willfp.libreforge.integrations.ecoitems

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.containsIgnoreCase
import com.willfp.ecoitems.items.ItemUtils
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.conditions.Condition
import org.bukkit.entity.Player

class ConditionHasEcoItem : Condition("has_ecoitem") {
    override fun isConditionMet(player: Player, config: Config): Boolean {
        return ItemUtils.getEcoItemsOnPlayer(player).map { it.id }.containsIgnoreCase(config.getString("item"))
    }

    override fun validateConfig(config: Config): List<ConfigViolation> {
        val violations = mutableListOf<ConfigViolation>()

        if (!config.has("item")) violations.add(
            ConfigViolation(
                "item",
                "You must specify the item!"
            )
        )

        return violations
    }
}
