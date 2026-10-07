package com.willfp.libreforge.conditions

import com.willfp.libreforge.plugin
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerChangedWorldEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * Permission results per player, kept only while LuckPerms reports permission changes. Read and
 * cleared from any thread.
 */
internal object PermissionCache : Listener {
    // Bounds staleness from changes LuckPerms does not report, such as op status.
    private const val MAX_AGE_MILLIS = 1000L

    private class PlayerPermissions(
        val createdAt: Long
    ) {
        val results = ConcurrentHashMap<String, Boolean>()
    }

    @Volatile
    private var isEnabled = false

    private val players = ConcurrentHashMap<UUID, PlayerPermissions>()

    /**
     * Start caching. Called once LuckPerms change events are subscribed to.
     */
    fun enable() {
        if (!isEnabled) {
            isEnabled = true
            plugin.eventManager.registerListener(this)
        }
    }

    /**
     * If [player] has [permission].
     */
    fun has(player: Player, permission: String): Boolean {
        if (!isEnabled) {
            return player.hasPermission(permission)
        }

        val now = System.currentTimeMillis()
        val uuid = player.uniqueId

        var permissions = players[uuid]
        if (permissions == null || now - permissions.createdAt >= MAX_AGE_MILLIS) {
            permissions = PlayerPermissions(now)
            players[uuid] = permissions
        }

        permissions.results[permission]?.let { return it }

        // An invalidation meanwhile drops this map, so a result from before it is never kept.
        return player.hasPermission(permission).also { permissions.results[permission] = it }
    }

    /**
     * Forget every result for the player with [uuid].
     */
    fun invalidate(uuid: UUID) {
        players.remove(uuid)
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onQuit(event: PlayerQuitEvent) {
        invalidate(event.player.uniqueId)
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onWorldChange(event: PlayerChangedWorldEvent) {
        invalidate(event.player.uniqueId)
    }
}
