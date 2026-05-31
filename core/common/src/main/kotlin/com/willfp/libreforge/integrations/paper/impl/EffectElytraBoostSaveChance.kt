package com.willfp.libreforge.integrations.paper.impl

import com.destroystokyo.paper.event.player.PlayerElytraBoostEvent
import com.willfp.libreforge.effects.templates.ChanceMultiplierEffect
import com.willfp.libreforge.toDispatcher
import org.bukkit.event.EventHandler

object EffectElytraBoostSaveChance : ChanceMultiplierEffect("elytra_boost_save_chance") {
    override val description = "Gives a percentage chance that a firework used for an elytra boost will not be consumed."
    override val categories = setOf("movement")
    @EventHandler(ignoreCancelled = true)
    fun handle(event: PlayerElytraBoostEvent) {
        val player = event.player

        if (passesChance(player.toDispatcher())) {
            event.setShouldConsume(false)
        }
    }
}
