package com.willfp.libreforge.integrations.ecobosses

import com.willfp.eco.core.integrations.mcmmo.McmmoManager
import com.willfp.ecobosses.bosses.Bosses
import com.willfp.ecobosses.bosses.EcoBoss
import com.willfp.ecobosses.events.BossTryDropItemEvent
import com.willfp.libreforge.effects.GenericMultiMultiplierEffect
import org.bukkit.event.EventHandler

class EffectBossDropChanceMultiplier : GenericMultiMultiplierEffect<EcoBoss>(
    "boss_drop_chance_multiplier",
    Bosses::getByID,
    Bosses::values,
    "bosses"
) {
    @EventHandler(ignoreCancelled = true)
    fun handle(event: BossTryDropItemEvent) {
        val player = event.player ?: return

        val multiplier = getMultiplier(player, event.boss)

        event.chance *= multiplier
    }
}
