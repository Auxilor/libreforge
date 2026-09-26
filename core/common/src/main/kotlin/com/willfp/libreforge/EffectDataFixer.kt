package com.willfp.libreforge

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

        // Disables every active effect with the provided holder it was enabled with.
        HolderStates.untrackPlayer(player)

        // Extra fix for pre-4.2.3
        player.fixAttributes()
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun scanOnJoin(event: PlayerJoinEvent) {
        val player = event.player

        // Extra fix for pre-4.2.3
        player.fixAttributes()

        // Effects are enabled in the next flush.
        HolderStates.trackPlayer(player)
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
