package com.willfp.libreforge.effects.effects

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.effects.Identifiers
import org.bukkit.entity.Player

class EffectFlight: Effect("flight", supportsFilters = true) {
    override fun handleEnable(player: Player, config: Config, identifiers: Identifiers) {
        player.allowFlight = true
    }

    override fun handleDisable(player: Player, identifiers: Identifiers) {
        player.allowFlight = false
        player.isFlying = false
    }
}