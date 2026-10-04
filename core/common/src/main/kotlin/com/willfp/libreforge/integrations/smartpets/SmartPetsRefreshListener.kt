package com.willfp.libreforge.integrations.smartpets

import com.smartpets.events.PetAdoptEvent
import com.smartpets.events.PetEvolveEvent
import com.smartpets.events.PetLevelUpEvent
import com.smartpets.events.PetRemoveEvent
import com.smartpets.events.PetSkillUnlockEvent
import com.smartpets.events.PetStatChangeEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener

object SmartPetsRefreshListener : Listener {
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun handle(event: PetAdoptEvent) {
        event.updateOwnerEffects()
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun handle(event: PetRemoveEvent) {
        event.updateOwnerEffects()
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun handle(event: PetLevelUpEvent) {
        event.updateOwnerEffects()
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun handle(event: PetEvolveEvent) {
        event.updateOwnerEffects()
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun handle(event: PetSkillUnlockEvent) {
        event.updateOwnerEffects()
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun handle(event: PetStatChangeEvent) {
        event.updateOwnerEffects()
    }
}
