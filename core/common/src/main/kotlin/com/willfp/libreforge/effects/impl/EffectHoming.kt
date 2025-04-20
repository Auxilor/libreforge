package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.entities.Entities
import com.willfp.eco.core.entities.TestableEntity
import com.willfp.eco.core.integrations.antigrief.AntigriefManager
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.arguments
import com.willfp.libreforge.distance
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.lerp
import com.willfp.libreforge.normalize
import com.willfp.libreforge.plugin
import com.willfp.libreforge.toFloat3
import com.willfp.libreforge.toVector
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.GameMode
import org.bukkit.entity.AbstractArrow
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.ProjectileLaunchEvent
import kotlin.math.abs

object EffectHoming : Effect<List<TestableEntity>>("homing") {
    override val arguments = arguments {
        require("distance", "You must specify the distance to hone from!")
    }

    override val parameters = setOf(
        TriggerParameter.PROJECTILE
    )

    private const val META_KEY_DISTANCE = "libreforge-homing-arrows-distance"
    private const val META_KEY_FORCE = "libreforge-homing-arrows-force"
    private const val META_KEY_TARGETS = "libreforge-homing-arrows-targets"
    private const val META_KEY_TRACKED = "libreforge-homing-arrows-tracked"
    private const val MAX_CHECKS = 10
    private const val CHECK_DELAY = 3L
    private const val SMOOTHNESS = 0.35f

    override fun onTrigger(config: Config, data: TriggerData, compileData: List<TestableEntity>): Boolean {
        val arrow = data.projectile as? AbstractArrow ?: return false

        var distance = config.getDoubleFromExpression("distance", data)

        if (distance < 0.5) {
            return false
        }

        val force = (arrow.velocity.clone().length() / 3).coerceAtMost(1.0).toFloat()
        distance *= force

        val targets = compileData.toMutableList()

        if (arrow.hasMetadata(META_KEY_DISTANCE)) {
            distance += arrow.getMetadata(META_KEY_DISTANCE).firstOrNull()?.value() as? Double ?: 0.0
            @Suppress("UNCHECKED_CAST")
            targets += arrow.getMetadata(META_KEY_DISTANCE).firstOrNull()?.value() as? List<TestableEntity>
                ?: emptyList()
        }

        arrow.setMetadata(META_KEY_DISTANCE, plugin.createMetadataValue(distance))
        arrow.setMetadata(META_KEY_FORCE, plugin.createMetadataValue(force))
        arrow.setMetadata(META_KEY_TARGETS, plugin.createMetadataValue(targets))

        return true
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    fun handle(event: ProjectileLaunchEvent) {
        val arrow = event.entity as? AbstractArrow ?: return
        val player = arrow.shooter as? Player ?: return

        var checks = 0

        if (arrow.hasMetadata(META_KEY_TRACKED)) {
            return
        }

        val distance = arrow.getMetadata(META_KEY_DISTANCE).firstOrNull()?.value() as? Double ?: return
        val force = arrow.getMetadata(META_KEY_FORCE).firstOrNull()?.value() as? Float ?: return

        @Suppress("UNCHECKED_CAST")
        val targets = arrow.getMetadata(META_KEY_TARGETS).firstOrNull()?.value() as? List<TestableEntity> ?: return

        arrow.setMetadata(META_KEY_TRACKED, plugin.createMetadataValue(true))

        plugin.runnableFactory.create { task ->
            checks++

            if (checks > MAX_CHECKS) {
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
                .filter { if (targets.isNotEmpty()) targets.any { t -> t.matches(it) } else true }
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
                arrow.velocity = lerp(arrow.velocity.toFloat3(), targetVelocity, 1 - SMOOTHNESS).toVector()
            }

        }.runTaskTimer(3L, CHECK_DELAY)
    }

    override fun makeCompileData(config: Config, context: ViolationContext): List<TestableEntity> {
        return config.getStrings("targets").map {
            Entities.lookup(it)
        }
    }
}
