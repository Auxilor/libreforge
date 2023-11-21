package com.willfp.libreforge

import com.willfp.libreforge.effects.Effects
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

object EffectCollisionFixer : Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    fun clearOnQuit(event: PlayerQuitEvent) {
        val player = event.player
        for ((holder, effects) in player.providedActiveEffects) {
            for (effect in effects) {
                effect.disable(PlayerDispatcher(player), holder)
            }
        }

        // Extra fix for pre-4.2.3
        player.fixAttributes()

        player.updateHolders()
        player.purgePreviousHolders()
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun scanOnJoin(event: PlayerJoinEvent) {
        val player = event.player

        // Extra fix for pre-4.2.3
        player.fixAttributes()

        player.updateHolders()

        plugin.scheduler.run {
            player.updateEffects()
        }
    }

    private fun Player.fixAttributes() {
        for (attribute in Attribute.values()) {
            val inst = this.getAttribute(attribute) ?: continue
            val mods = inst.modifiers.filter { it.name.startsWith("libreforge") }
            for (mod in mods) {
                inst.removeModifier(mod)
            }
        }

        // Extra fix
        if (this.health > (this.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.value ?: 0.0)) {
            this.health = this.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.value ?: 0.0
        }

        // Legacy fix
        for (effect in Effects.values()) {
            for (attribute in Attribute.values()) {
                val inst = this.getAttribute(attribute) ?: continue
                val mods = inst.modifiers.filter { it.name.startsWith(effect.id) }
                for (mod in mods) {
                    inst.removeModifier(mod)
                }
            }
        }
    }
}
