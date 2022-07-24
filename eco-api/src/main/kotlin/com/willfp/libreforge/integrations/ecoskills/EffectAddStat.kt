package com.willfp.libreforge.integrations.ecoskills

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.ecoskills.api.EcoSkillsAPI
import com.willfp.ecoskills.api.modifier.PlayerStatModifier
import com.willfp.ecoskills.stats.Stats
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.effects.Identifiers
import org.bukkit.entity.Player

class EffectAddStat : Effect("add_stat") {
    private val api = EcoSkillsAPI.getInstance()

    override fun handleEnable(
        player: Player,
        config: Config,
        identifiers: Identifiers
    ) {
        api.addStatModifier(
            player,
            PlayerStatModifier(
                identifiers.key,
                Stats.getByID(config.getString("stat")) ?: Stats.STRENGTH,
                config.getIntFromExpression("amount", player)
            )
        )
    }

    override fun handleDisable(
        player: Player,
        identifiers: Identifiers
    ) {
        api.removeStatModifier(
            player,
            identifiers.key
        )
    }

    override fun validateConfig(config: Config): List<ConfigViolation> {
        val violations = mutableListOf<ConfigViolation>()

        if (!config.has("stat")) violations.add(
            ConfigViolation(
                "stat",
                "You must specify the stat!"
            )
        )

        if (!config.has("amount")) violations.add(
            ConfigViolation(
                "amount",
                "You must specify the amount to add/remove!"
            )
        )

        return violations
    }
}