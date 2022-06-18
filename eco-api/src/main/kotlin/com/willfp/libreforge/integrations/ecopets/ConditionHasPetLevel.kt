package com.willfp.libreforge.integrations.ecopets

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.ecopets.api.EcoPetsAPI
import com.willfp.ecopets.pets.Pets
import com.willfp.ecoskills.api.PlayerSkillLevelUpEvent
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.updateEffects
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority

class ConditionHasPetLevel : Condition("has_pet_level") {
    @EventHandler(
        priority = EventPriority.MONITOR,
        ignoreCancelled = true
    )
    fun handle(event: PlayerSkillLevelUpEvent) {
        val player = event.player

        player.updateEffects()
    }

    override fun isConditionMet(player: Player, config: Config): Boolean {
        return EcoPetsAPI.instance.getPetLevel(
            player,
            Pets.getByID(config.getString("pet").lowercase()) ?: return false
        ) >= config.getIntFromExpression("level", player)
    }

    override fun validateConfig(config: Config): List<ConfigViolation> {
        val violations = mutableListOf<ConfigViolation>()

        if (!config.has("pet")) violations.add(
            ConfigViolation(
                "pet",
                "You must specify the pet!"
            )
        )

        if (!config.has("level")) violations.add(
            ConfigViolation(
                "level",
                "You must specify the pet level!"
            )
        )

        return violations
    }
}
