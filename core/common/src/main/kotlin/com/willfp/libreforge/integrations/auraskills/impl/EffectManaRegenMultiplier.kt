package com.willfp.libreforge.integrations.auraskills.impl

import com.willfp.libreforge.effects.templates.MultiplierEffect
import com.willfp.libreforge.toDispatcher
import dev.aurelium.auraskills.api.event.mana.ManaRegenerateEvent
import org.bukkit.event.EventHandler

object EffectManaRegenMultiplier : MultiplierEffect("mana_regen_multiplier") {
    @EventHandler(ignoreCancelled = true)
    fun handle(event: ManaRegenerateEvent) {
        val player = event.player

        event.amount *= getMultiplier(player.toDispatcher())
    }
}
