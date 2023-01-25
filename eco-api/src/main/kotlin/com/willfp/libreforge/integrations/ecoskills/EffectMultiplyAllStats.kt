package com.willfp.libreforge.integrations.ecoskills

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.ecoskills.api.EcoSkillsAPI
import com.willfp.ecoskills.api.modifier.ModifierOperation
import com.willfp.ecoskills.api.modifier.PlayerStatModifier
import com.willfp.ecoskills.stats.Stats
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.effects.IDGenerator
import com.willfp.libreforge.effects.Identifiers
import org.bukkit.entity.Player

class EffectMultiplyAllStats : Effect("multiply_all_stats") {
    override val arguments = arguments {
        require("multiplier", "You must specify the multiplier!")
    }

    private val api = EcoSkillsAPI.getInstance()

    override fun handleEnable(
        player: Player,
        config: Config,
        identifiers: Identifiers
    ) {
        val generator = IDGenerator(identifiers.uuid)

        for ((offset, stat) in Stats.values().withIndex()) {
            api.addStatModifier(
                player,
                PlayerStatModifier(
                    generator.makeIdentifiers(offset).key,
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
        val generator = IDGenerator(identifiers.uuid)

        for (offset in Stats.values().indices) {
            api.removeStatModifier(
                player,
                generator.makeIdentifiers(offset).key
            )
        }
    }
}
