package com.willfp.libreforge

import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent
import com.willfp.libreforge.EmptyProvidedHolder.holder
import com.willfp.libreforge.effects.Effects
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.metadata.FixedMetadataValue

object EffectDataFixer : Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    fun onJoin(event: PlayerJoinEvent) {
        if(isFirstJoin(event.player))return;
        cleanup(event.player)
    }
    fun isFirstJoin(player: Player): Boolean {
        return player.getMetadata("firstJoin").isEmpty()
    }
    fun cleanup(player: Player) {
        player.setMetadata("firstjoin",FixedMetadataValue(plugin,"false"))
        player.fixAttributes()
    }

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
