package com.willfp.libreforge.integrations.aureliumskills

import com.archyx.aureliumskills.api.AureliumAPI
import com.willfp.libreforge.events.EffectActivateEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class UseManaHandler : Listener {
    @EventHandler
    fun onTrigger(event: EffectActivateEvent) {
        val player = event.player
        val config = event.config
        val effect = event.effect
        val cost = config.getDoubleFromExpression("mana_cost", player)

        if (config.has("mana_cost")) {
            if (AureliumAPI.getMana(player) < cost) {
                effect.sendCannotAffordTypeMessage(player, cost, "mana")
                event.isCancelled = true
                return
            }

            AureliumAPI.setMana(player, AureliumAPI.getMana(player) - cost)
        }
    }
}