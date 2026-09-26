package com.willfp.libreforge.integrations.citizens

import com.willfp.eco.core.EcoPlugin
import com.willfp.libreforge.HolderStates
import com.willfp.libreforge.filters.Filters
import com.willfp.libreforge.integrations.LoadableIntegration
import com.willfp.libreforge.integrations.citizens.impl.FilterNPC
import com.willfp.libreforge.integrations.citizens.impl.TriggerLeftClickNPC
import com.willfp.libreforge.integrations.citizens.impl.TriggerRightClickNPC
import com.willfp.libreforge.triggers.Triggers
import net.citizensnpcs.api.event.NPCDespawnEvent
import net.citizensnpcs.api.event.NPCSpawnEvent
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener

object CitizensIntegration : LoadableIntegration {
    override fun load(plugin: EcoPlugin) {
        Triggers.register(TriggerLeftClickNPC)
        Triggers.register(TriggerRightClickNPC)
        Filters.register(FilterNPC)

        plugin.eventManager.registerListener(NPCHolderListener)
    }

    override fun getPluginName(): String {
        return "Citizens"
    }

    /**
     * Player NPCs fire no join, spawn or remove event, so they are tracked from Citizens' own events.
     */
    private object NPCHolderListener : Listener {
        @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
        fun onSpawn(event: NPCSpawnEvent) {
            val entity = event.npc.entity as? Player ?: return
            HolderStates.trackEntity(entity, isNPC = true)
        }

        @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
        fun onDespawn(event: NPCDespawnEvent) {
            val entity = event.npc.entity as? Player ?: return
            HolderStates.untrackEntity(entity, isNPC = true)
        }
    }
}
