package com.willfp.libreforge.integrations.ecoskills

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.ecoskills.api.EcoSkillsAPI
import com.willfp.ecoskills.api.PlayerSkillLevelUpEvent
import com.willfp.ecoskills.skills.Skills
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.updateEffects
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority


class ConditionHasSkillLevel : Condition("has_skill_level") {
    @EventHandler(
        priority = EventPriority.MONITOR,
        ignoreCancelled = true
    )
    fun handle(event: PlayerSkillLevelUpEvent) {
        val player = event.player

        player.updateEffects()
    }

    override fun isConditionMet(player: Player, config: Config): Boolean {
        return EcoSkillsAPI.getInstance().getSkillLevel(
            player,
            Skills.getByID(config.getString("skill").lowercase()) ?: Skills.COMBAT
        ) >= config.getInt("level")
    }

    override fun validateConfig(config: Config): List<ConfigViolation> {
        val violations = mutableListOf<ConfigViolation>()

        config.getStringOrNull("skill")
            ?: violations.add(
                ConfigViolation(
                    "skill",
                    "You must specify the skill!"
                )
            )

        config.getIntOrNull("level")
            ?: violations.add(
                ConfigViolation(
                    "level",
                    "You must specify the skill level!"
                )
            )

        return violations
    }
}