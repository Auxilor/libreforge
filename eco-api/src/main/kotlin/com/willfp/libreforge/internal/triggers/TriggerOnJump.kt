package com.willfp.libreforge.internal.triggers

import com.willfp.eco.core.events.PlayerJumpEvent
import com.willfp.eco.core.integrations.antigrief.AntigriefManager
import com.willfp.eco.core.integrations.mcmmo.McmmoManager
import com.willfp.eco.util.NumberUtils
import com.willfp.libreforge.api.events.EffectActivateEvent
import com.willfp.libreforge.api.getHolders
import com.willfp.libreforge.api.triggers.Trigger
import org.bukkit.entity.Arrow
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.entity.Trident
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent

class TriggerOnJump : Trigger("on_jump") {
    @EventHandler(ignoreCancelled = true)
    fun onJump(event: PlayerJumpEvent) {
        if (McmmoManager.isFake(event)) {
            return
        }
        val player = event.player

        for (holder in player.getHolders()) {
            for ((effect, config, filter, triggers) in holder.effects) {
                if (NumberUtils.randFloat(0.0, 100.0) > (config.getDoubleOrNull("chance") ?: 100.0)) {
                    continue
                }

                if (!triggers.contains(this)) {
                    continue
                }

                val aEvent = EffectActivateEvent(player, holder, effect)
                this.plugin.server.pluginManager.callEvent(aEvent)
                if (!aEvent.isCancelled) {
                    effect.onJump(player, event, config)
                }
            }
        }
    }
}
