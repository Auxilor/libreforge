package com.willfp.libreforge.integrations.ecoskills

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.ecoskills.api.EcoSkillsAPI
import com.willfp.ecoskills.api.modifier.ModifierOperation
import com.willfp.ecoskills.api.modifier.PlayerStatModifier
import com.willfp.ecoskills.stats.Stats
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.effects.Identifiers
import org.bukkit.entity.Player

class EffectMultiplyStat : Effect("multiply_stat") {
    override val arguments = arguments {
        require("stat", "You must specify the stat!")
        require("multiplier", "You must specify the multiplier!")
    }

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
                config.getDoubleFromExpression("multiplier", player),
                ModifierOperation.MULTIPLY
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
}
