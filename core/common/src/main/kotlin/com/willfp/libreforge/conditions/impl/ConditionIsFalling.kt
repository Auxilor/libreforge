package com.willfp.libreforge.conditions.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.get
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.updateEffects
import org.bukkit.entity.Entity
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

object ConditionIsFalling : Condition<NoCompileData>("is_falling") {
    private val fallingPlayers = ConcurrentHashMap.newKeySet<UUID>()

    override val description = "Passes when the entity has a significant downward velocity, indicating it is falling."

    override val categories = setOf("player")

    override val additionalInfo = listOf(
        "Detected via a downward Y-velocity threshold rather than a falling flag — may briefly trigger at the apex of a jump."
    )

    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ): Boolean {
        val entity = dispatcher.get<Entity>() ?: return false

        return isFalling(entity)
    }

    private fun isFalling(entity: Entity) = entity.velocity.y < -0.1

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun handle(event: PlayerMoveEvent) {
        val player = event.player

        val changed = if (isFalling(player)) {
            fallingPlayers.add(player.uniqueId)
        } else {
            fallingPlayers.remove(player.uniqueId)
        }

        if (changed) {
            player.toDispatcher().updateEffects()
        }
    }

    @EventHandler
    fun handle(event: PlayerQuitEvent) {
        fallingPlayers.remove(event.player.uniqueId)
    }
}
