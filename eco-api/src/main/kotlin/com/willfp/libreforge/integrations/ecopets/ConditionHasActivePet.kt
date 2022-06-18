package com.willfp.libreforge.integrations.ecopets

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.ecopets.api.EcoPetsAPI
import com.willfp.ecopets.pets.Pets
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.conditions.Condition
import org.bukkit.entity.Player

class ConditionHasActivePet : Condition("has_active_pet") {
    override fun isConditionMet(player: Player, config: Config): Boolean {
        return EcoPetsAPI.instance.hasPet(
            player,
            Pets.getByID(config.getString("pet").lowercase()) ?: return false
        )
    }

    override fun validateConfig(config: Config): List<ConfigViolation> {
        val violations = mutableListOf<ConfigViolation>()

        if (!config.has("pet")) violations.add(
            ConfigViolation(
                "pet",
                "You must specify the pet!"
            )
        )

        return violations
    }
}
