package com.willfp.libreforge.effects.impl.bossbar

import com.willfp.eco.core.map.concurrentListMap
import com.willfp.eco.util.asAudience
import java.util.UUID
import net.kyori.adventure.bossbar.BossBar
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent

object BossBars : Listener {
    private val registry = concurrentListMap<String, RegistrableBossBar>()

    fun register(bossBar: RegistrableBossBar) {
        remove(bossBar.id, bossBar.uuid)

        registry[bossBar.id].add(bossBar)
        val player = Bukkit.getPlayer(bossBar.uuid) ?: return
        bossBar.bossBar.addViewer(player.asAudience())
    }

    fun remove(id: String) {
        for (bossBar in registry[id]) {
            val player = Bukkit.getPlayer(bossBar.uuid) ?: continue
            bossBar.bossBar.removeViewer(player.asAudience())
        }

        registry.remove(id)
    }

    fun remove(id: String, uuid: UUID) {
        val bossBars = registry[id]

        for (bossBar in bossBars.filter { it.uuid == uuid }) {
            Bukkit.getPlayer(uuid)?.let { bossBar.bossBar.removeViewer(it.asAudience()) }
            bossBars.remove(bossBar)
        }

        if (bossBars.isEmpty()) {
            registry.remove(id)
        }
    }

    fun update(id: String, transform: (BossBar) -> Unit) {
        for (bossBar in registry[id]) {
            bossBar.bossBar.apply(transform)
        }
    }

    operator fun get(id: String): List<RegistrableBossBar> {
        return getByID(id)
    }

    fun getByID(id: String): List<RegistrableBossBar> {
        return registry[id].toList()
    }

    fun values(): List<RegistrableBossBar> {
        return registry.values.flatten()
    }

    @EventHandler
    fun handleQuit(event: PlayerQuitEvent) {
        for (id in registry.keys.toList()) {
            remove(id, event.player.uniqueId)
        }
    }
}
