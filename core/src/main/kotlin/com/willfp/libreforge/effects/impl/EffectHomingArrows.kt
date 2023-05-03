package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.integrations.antigrief.AntigriefManager
import com.willfp.eco.core.map.listMap
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.arguments
import com.willfp.libreforge.distance
import com.willfp.libreforge.effects.AdditionModifier
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.effects.Identifiers
import com.willfp.libreforge.lerp
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
import kotlin.math.abs

object EffectHomingArrows : Effect<NoCompileData>("homing_arrows") {
    override val arguments = arguments {
        require("distance", "You must specify the distance to hone from!")
    }

    private val modifiers = listMap<UUID, AdditionModifier>()
    private const val maxChecks = 10
    private const val checkDelay = 3L
    private const val smoothness = 0.35f

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

        if (distance < 0.5) {
            return
        }

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

            val entities = arrow.getNearbyEntities(distance, distance, distance)
                .asSequence()
                .filterIsInstance<LivingEntity>()
                .filterNot { it.uniqueId == player.uniqueId }
                .filterNot { it.type in setOf(EntityType.ENDERMAN, EntityType.ARMOR_STAND) }
                .filterNot { it.isDead }
                .filter { AntigriefManager.canInjure(player, it) }
                .filter { if (it is Player) it.gameMode in setOf(GameMode.ADVENTURE, GameMode.SURVIVAL) else true }
                .sortedBy { it.location.distanceSquared(arrow.location) }

            for (entity in entities) {
                val dist = arrow.location.toFloat3().distance(entity.eyeLocation.toFloat3())

                if (dist < 1.0) {
                    task.cancel()
                    break
                }

                val vector = entity.eyeLocation.toFloat3() - arrow.location.toFloat3()
                val normalized = vector.normalize()

                if (arrow.location.world.rayTraceBlocks(
                        arrow.location,
                        normalized.toVector(),
                        dist.toDouble()
                    )?.hitBlock?.isLiquid == false
                ) {
                    continue
                }

                val targetVelocity = normalized * force

                val angle = abs(Math.toDegrees(arrow.velocity.angle(targetVelocity.toVector()).toDouble()))

                if (angle > 90) {
                    continue
                }

                // Lerp between the current velocity and the target velocity
                arrow.velocity = lerp(arrow.velocity.toFloat3(), targetVelocity, 1 - smoothness).toVector()
            }

        }.runTaskTimer(3L, checkDelay)
    }
}
