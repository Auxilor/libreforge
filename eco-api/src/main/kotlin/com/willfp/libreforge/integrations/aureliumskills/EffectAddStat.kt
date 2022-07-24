package com.willfp.libreforge.integrations.aureliumskills

import com.archyx.aureliumskills.api.AureliumAPI
import com.archyx.aureliumskills.stats.Stats
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.effects.Identifiers
import org.bukkit.entity.Player

class EffectAddStat : Effect("add_stat") {
    override fun handleEnable(
        player: Player,
        config: Config,
        identifiers: Identifiers
    ) {
        AureliumAPI.addStatModifier(
            player,
            identifiers.key.key,
            Stats.valueOf(config.getString("stat").uppercase()),
            config.getDoubleFromExpression("amount", player)
        )
    }

    override fun handleDisable(
        player: Player,
        identifiers: Identifiers
    ) {
        AureliumAPI.removeStatModifier(
            player,
            identifiers.key.key,
        )
    }
}
