package com.willfp.libreforge.integrations.ecoskills

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.ecoskills.api.EcoSkillsAPI
import com.willfp.ecoskills.api.modifier.ModifierOperation
import com.willfp.ecoskills.api.modifier.PlayerStatModifier
import com.willfp.ecoskills.stats.Stats
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.effects.getEffectAmount
import org.bukkit.entity.Player

class EffectMultiplyStat : Effect("multiply_stat") {
    private val api = EcoSkillsAPI.getInstance()

    override fun handleEnable(
        player: Player,
        config: Config
    ) {
        api.addStatModifier(
            player,
            PlayerStatModifier(
                this.getNamespacedKey(player.getEffectAmount(this)),
                Stats.getByID(config.getString("stat")) ?: Stats.STRENGTH,
                config.getDouble("multiplier"),
                ModifierOperation.MULTIPLY
            )
        )
    }

    override fun handleDisable(player: Player) {
        api.removeStatModifier(
            player,
            this.getNamespacedKey(player.getEffectAmount(this))
        )
    }

    override fun validateConfig(config: Config): List<ConfigViolation> {
        val violations = mutableListOf<ConfigViolation>()

        config.getStringOrNull("stat")
            ?: violations.add(
                ConfigViolation(
                    "stat",
                    "You must specify the stat!"
                )
            )

        config.getDoubleOrNull("multiplier")
            ?: violations.add(
                ConfigViolation(
                    "multiplier",
                    "You must specify the multiplier!"
                )
            )

        return violations
    }
}