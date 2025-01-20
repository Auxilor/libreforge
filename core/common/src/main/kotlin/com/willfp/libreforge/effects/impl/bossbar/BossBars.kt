package com.willfp.libreforge.effects.impl.bossbar

import com.willfp.eco.core.map.listMap
import com.willfp.eco.util.asAudience
import net.kyori.adventure.bossbar.BossBar
import org.bukkit.Bukkit

object BossBars {
    private val registry = listMap<String, RegistrableBossBar>()

    fun register(bossBar: RegistrableBossBar) {
        if (registry[bossBar.id].any { it.uuid == bossBar.uuid }) {
            return
        }

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
}
