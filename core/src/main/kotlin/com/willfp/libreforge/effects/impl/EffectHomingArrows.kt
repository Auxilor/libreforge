package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.integrations.antigrief.AntigriefManager
import com.willfp.eco.core.map.listMap
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.AdditionModifier
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.effects.Identifiers
import com.willfp.libreforge.normalize
import com.willfp.libreforge.plugin
import com.willfp.libreforge.toFloat3
import com.willfp.libreforge.toVector
import org.bukkit.GameMode
import org.bukkit.entity.AbstractArrow
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.entity.Trident
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.ProjectileLaunchEvent
import java.util.UUID

object EffectHomingArrows : Effect<NoCompileData>("homing_arrows") {
    override val arguments = arguments {
        require("distance", "You must specify the distance to hone from!")
    }

    private val modifiers = listMap<UUID, AdditionModifier>()
    private const val maxChecks = 10
    private const val checkDelay = 10L

    override fun onEnable(
        player: Player,
        config: Config,
        identifiers: Identifiers,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ) {
        modifiers[player.uniqueId] += AdditionModifier(identifiers.uuid) {
            config.getDoubleFromExpression("distance", player)
        }
    }

    override fun onDisable(player: Player, identifiers: Identifiers, holder: ProvidedHolder) {
        modifiers[player.uniqueId].removeIf { it.uuid == identifiers.uuid }
    }

    @EventHandler(ignoreCancelled = true)
    fun handle(event: ProjectileLaunchEvent) {
        val arrow = event.entity as? AbstractArrow ?: return
        val player = arrow.shooter as? Player ?: return

        if (arrow is Trident) {
            return
        }

        var distance = modifiers[player.uniqueId].sumOf { it.bonus }

        val force = (arrow.velocity.clone().length() / 3).coerceAtMost(1.0).toFloat()
        distance *= force

        var checks = 0

        plugin.runnableFactory.create { task ->
            checks++

            if (checks > maxChecks) {
                task.cancel()
            }

            if (arrow.isDead || arrow.isInBlock || arrow.isOnGround) {
                task.cancel()
            }

            val entity = arrow.getNearbyEntities(distance, distance, distance)
                .asSequence()
                .filterIsInstance<LivingEntity>()
                .filterNot { it == player }
                .filterNot { it.type == EntityType.ENDERMAN }
                .filter { AntigriefManager.canInjure(player, it) }
                .filter { if (it is Player) it.gameMode in setOf(GameMode.ADVENTURE, GameMode.SURVIVAL) else true }
                .sortedBy { it.location.distanceSquared(arrow.location) }
                .firstOrNull()

            if (entity != null) {
                val vector = entity.location.toFloat3() - arrow.location.toFloat3()
                arrow.velocity = (vector.normalize() * force).toVector()
            }
        }.runTaskTimer(3L, checkDelay)
    }
}
