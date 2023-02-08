package com.willfp.libreforge.integrations.ecoskills.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.ecoskills.api.EcoSkillsAPI
import com.willfp.ecoskills.api.modifier.ModifierOperation
import com.willfp.ecoskills.api.modifier.PlayerStatModifier
import com.willfp.ecoskills.stats.Stats
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.effects.Identifiers
import org.bukkit.entity.Player

object EffectMultiplyAllStats : Effect<NoCompileData>("multiply_all_stats") {
    override val arguments = arguments {
        require("multiplier", "You must specify the multiplier!")
    }

    override fun onEnable(player: Player, config: Config, identifiers: Identifiers, compileData: NoCompileData) {
        val factory = identifiers.makeFactory()

        for ((offset, stat) in Stats.values().withIndex()) {
            EcoSkillsAPI.getInstance().addStatModifier(
                player,
                PlayerStatModifier(
                    factory.makeIdentifiers(offset).key,
                    stat,
                    config.getDoubleFromExpression("multiplier", player),
                    ModifierOperation.MULTIPLY
                )
            )
        }
    }

    override fun onDisable(player: Player, identifiers: Identifiers) {
        val factory = identifiers.makeFactory()

        for (offset in Stats.values().indices) {
            EcoSkillsAPI.getInstance().removeStatModifier(
                player,
                factory.makeIdentifiers(offset).key
            )
        }
    }
}
