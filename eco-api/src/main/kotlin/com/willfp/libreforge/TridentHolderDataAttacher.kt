package com.willfp.libreforge

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.integrations.mcmmo.McmmoManager
import org.bukkit.entity.Player
import org.bukkit.entity.Trident
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.ProjectileLaunchEvent

class TridentHolderDataAttacher(
    private val plugin: EcoPlugin
) : Listener {
    @EventHandler(ignoreCancelled = true)
    fun onProjectileLaunch(event: ProjectileLaunchEvent) {
        if (McmmoManager.isFake(event)) {
            return
        }

        val shooter = event.entity.shooter as? Player ?: return
        val trident = event.entity

        if (trident !is Trident) {
            return
        }

        trident.setMetadata(
            "${plugin.name.lowercase()}_trident_holders",
            plugin.metadataValueFactory.create(
                shooter.getHolders()
            )
        )
    }
}

fun Trident.getAttachedHolders(): Iterable<Holder> {
    @Suppress("UNCHECKED_CAST")
    return this.getMetadata("${LibReforgePlugin.instance.name.lowercase()}_trident_holders")
        .firstOrNull()?.value() as? Iterable<Holder> ?: emptyList()
}
