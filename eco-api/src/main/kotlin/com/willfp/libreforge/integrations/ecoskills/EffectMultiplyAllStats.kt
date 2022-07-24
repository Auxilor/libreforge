package com.willfp.libreforge.integrations.ecoskills

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.ecoskills.api.EcoSkillsAPI
import com.willfp.ecoskills.api.modifier.ModifierOperation
import com.willfp.ecoskills.api.modifier.PlayerStatModifier
import com.willfp.ecoskills.stats.Stats
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.effects.Identifiers
import org.bukkit.entity.Player

class EffectMultiplyAllStats : Effect("multiply_all_stats") {
    private val api = EcoSkillsAPI.getInstance()

    override fun handleEnable(
        player: Player,
        config: Config,
        identifiers: Identifiers
    ) {
        for (stat in Stats.values()) {
            api.addStatModifier(
                player,
                PlayerStatModifier(
                    identifiers.key,
                    stat,
                    config.getDoubleFromExpression("multiplier", player),
                    ModifierOperation.MULTIPLY
                )
            )
        }
    }

    override fun handleDisable(
        player: Player,
        identifiers: Identifiers
    ) {
        for (stat in Stats.values()) {
            api.removeStatModifier(
                player,
                identifiers.key
            )
        }
    }

    override fun validateConfig(config: Config): List<ConfigViolation> {
        val violations = mutableListOf<ConfigViolation>()

        if (!config.has("multiplier")) violations.add(
            ConfigViolation(
                "multiplier",
                "You must specify the multiplier!"
            )
        )

        return violations
    }
}