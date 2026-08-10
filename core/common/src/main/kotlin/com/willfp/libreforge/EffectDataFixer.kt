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

    private val MODIFIER_PATTERN = Regex("\\d+_\\d+")

    @EventHandler(priority = EventPriority.LOWEST)
    fun clearOnQuit(event: PlayerQuitEvent) {
        val player = event.player
        val dispatcher = player.toDispatcher()

        for ((block, occurrence) in dispatcher.providedActiveEffects.withOccurrences()) {
            val (effect, holder) = block
            effect.disable(dispatcher, holder, occurrence = occurrence)
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
                if (mod.key.namespace == "eco" && mod.key.key.matches(MODIFIER_PATTERN)) {
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
