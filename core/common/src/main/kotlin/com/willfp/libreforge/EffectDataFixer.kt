package com.willfp.libreforge

import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent
import org.bukkit.Registry
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

object EffectDataFixer : Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    fun clearOnQuit(event: PlayerQuitEvent) {
        val player = event.player
        val dispatcher = player.toDispatcher()

        for ((effect, holder) in dispatcher.providedActiveEffects) {
            effect.disable(dispatcher, holder)
        }

        // Extra fix for pre-4.2.3
        player.fixAttributes()

        dispatcher.updateHolders()
        dispatcher.purgePreviousHolders()
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun scanOnJoin(event: PlayerJoinEvent) {
        val player = event.player
        val dispatcher = player.toDispatcher()

        // Extra fix for pre-4.2.3
        player.fixAttributes()

        dispatcher.updateHolders()

        plugin.scheduler.run {
            dispatcher.updateEffects()
        }
    }

    private fun Player.fixAttributes() {
        for (attribute in Registry.ATTRIBUTE) {
            val inst = this.getAttribute(attribute) ?: continue
            for (mod in inst.modifiers.toList()) {
                val key = mod.key
                // lf_ prefix = new format; bare digits_digits = old format pre-fix
                if (key.namespace == "eco" && (
                    key.key.startsWith("lf_") ||
                    key.key.matches(Regex("\\d+_\\d+"))
                )) {
                    inst.removeModifier(mod)
                }
            }
        }

        // Extra fix
        val maxHealth = this.getAttribute(Attribute.MAX_HEALTH)?.value ?: 0.0
        if (this.health > maxHealth) {
            this.health = maxHealth
        }
    }
}

object PaperEffectDataFixer : Listener {
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    fun purgeOnRemove(event: EntityRemoveFromWorldEvent) {
        if (event.entity is Player) {
            return
        }

        val dispatcher = event.entity.toDispatcher()
        dispatcher.purgePreviousHolders()
    }
}
