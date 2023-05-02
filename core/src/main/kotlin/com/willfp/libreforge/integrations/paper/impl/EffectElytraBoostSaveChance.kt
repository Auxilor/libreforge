package com.willfp.libreforge.integrations.paper.impl

import com.destroystokyo.paper.event.player.PlayerElytraBoostEvent
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.map.listMap
import com.willfp.eco.util.NumberUtils
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.effects.Identifiers
import com.willfp.libreforge.effects.MultiplierModifier
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import java.util.UUID

object EffectElytraBoostSaveChance : Effect<NoCompileData>("elytra_boost_save_chance") {
    override val arguments = arguments {
        require("chance", "You must specify the chance to not consume rockets!")
    }

    private val modifiers = listMap<UUID, MultiplierModifier>()

    override fun onEnable(player: Player, config: Config, identifiers: Identifiers, holder: ProvidedHolder, compileData: NoCompileData) {
        modifiers[player.uniqueId] += MultiplierModifier(identifiers.uuid) {
            config.getDoubleFromExpression("chance", player)
        }
    }

    override fun onDisable(player: Player, identifiers: Identifiers, holder: ProvidedHolder) {
        modifiers[player.uniqueId].removeIf { it.uuid == identifiers.uuid }
    }

    @EventHandler(ignoreCancelled = true)
    fun handle(event: PlayerElytraBoostEvent) {
        val player = event.player

        var chance = 1.0

        for (modifier in modifiers[player.uniqueId]) {
            chance *= (100 - modifier.multiplier) / 100
        }

        if (NumberUtils.randFloat(0.0, 1.0) > chance) {
            event.setShouldConsume(false)
        }
    }
}
