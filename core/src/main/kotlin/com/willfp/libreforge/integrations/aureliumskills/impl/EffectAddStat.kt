package com.willfp.libreforge.integrations.aureliumskills.impl

import com.archyx.aureliumskills.api.AureliumAPI
import com.archyx.aureliumskills.stats.Stats
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.effects.Identifiers
import org.bukkit.entity.Player

object EffectAddStat : Effect<NoCompileData>("add_stat") {
    override val arguments = arguments {
        require("stat", "You must specify the stat!")
        require("amount", "You must specify the amount!")
    }

    override fun onEnable(player: Player, config: Config, identifiers: Identifiers, holder: ProvidedHolder, compileData: NoCompileData) {
        AureliumAPI.addStatModifier(
            player,
            identifiers.key.key,
            Stats.valueOf(config.getString("stat").uppercase()),
            config.getDoubleFromExpression("amount", player)
        )
    }

    override fun onDisable(player: Player, identifiers: Identifiers, holder: ProvidedHolder) {
        AureliumAPI.removeStatModifier(
            player,
            identifiers.key.key,
        )
    }
}
