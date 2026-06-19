package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.entities.Entities
import com.willfp.eco.core.entities.TestableEntity
import com.willfp.eco.core.integrations.antigrief.AntigriefManager
import com.willfp.libreforge.ArgType
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
import dev.romainguy.kotlin.math.Float3
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.sqrt

object EffectHoming : Effect<List<TestableEntity>>("homing") {
    override val description = "Makes fired arrows home in on the nearest valid target within range."
    override val categories = setOf("combat")

    override val arguments = arguments {
        require(
            "distance",
            "You must specify the distance to hone from!",
            description = "The maximum range at which the arrow will lock on to a target. Supports expressions.",
            type = ArgType.EXPRESSION
        )
        optional(
            "targets",
            description = "A list of entity types the arrow will home in on. Leave empty to target all entities.",
            type = ArgType.ENTITY_LIST,
            default = "[]"
        )
    }

    override val parameters = setOf(
        TriggerParameter.PROJECTILE
    )

    private const val META_KEY_DISTANCE = "libreforge-homing-arrows-distance"
    private const val META_KEY_TARGETS = "libreforge-homing-arrows-targets"
    private const val META_KEY_TRACKED = "libreforge-homing-arrows-tracked"
    private const val MAX_CHECKS = 40
    private const val CHECK_DELAY = 1L

    // How much of the old velocity is kept each tick. Higher = smoother, gentler curve;
    // lower = sharper, snappier turns. Since we now correct every tick (CHECK_DELAY = 1)
    // this is kept high so the trajectory curves smoothly instead of jerking each tick.
    private const val SMOOTHNESS = 0.8f

    // Maximum lead time (in ticks) used when predicting a target's future position.
    // Stops wildly distant predictions when a target is moving very fast.
    private const val MAX_LEAD_TICKS = 20f

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
            targets += arrow.getMetadata(META_KEY_TARGETS).firstOrNull()?.value() as? List<TestableEntity>
                ?: emptyList()
        }

        arrow.setMetadata(META_KEY_DISTANCE, plugin.createMetadataValue(distance))
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

        @Suppress("UNCHECKED_CAST")
        val targets = arrow.getMetadata(META_KEY_TARGETS).firstOrNull()?.value() as? List<TestableEntity> ?: return

        arrow.setMetadata(META_KEY_TRACKED, plugin.createMetadataValue(true))

        plugin.runnableFactory.create { task ->
            checks++

            if (checks > MAX_CHECKS || arrow.isDead || arrow.isInBlock || arrow.isOnGround) {
                task.cancel()
                return@create
            }

            val arrowPos = arrow.location.toFloat3()

            // Preserve the arrow's current speed so homing only ever changes direction,
            // never slows the projectile down (which made it lag behind fast targets).
            val speed = arrow.velocity.length().toFloat()

            if (speed < 1e-3f) {
                return@create
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

            // Lock onto a single best target rather than blending toward every nearby
            // entity (the previous loop steered toward the *last* one processed).
            for (entity in entities) {
                val targetPos = entity.eyeLocation.toFloat3()
                val dist = arrowPos.distance(targetPos)

                if (dist < 1.0f) {
                    task.cancel()
                    return@create
                }

                // Line of sight: skip targets hidden behind solid blocks.
                if (arrow.location.world.rayTraceBlocks(
                        arrow.location,
                        (targetPos - arrowPos).normalize().toVector(),
                        dist.toDouble()
                    )?.hitBlock?.isLiquid == false
                ) {
                    continue
                }

                // Aim ahead of the target so a moving target is intercepted, not chased.
                val aimPoint = predictIntercept(arrowPos, speed, targetPos, entity.velocity.toFloat3())
                val direction = (aimPoint - arrowPos).normalize()

                // Don't U-turn onto targets that are behind the arrow.
                val angle = abs(Math.toDegrees(arrow.velocity.angle(direction.toVector()).toDouble()))

                if (angle > 90) {
                    continue
                }

                // Steer toward the intercept direction, then renormalize to the original
                // speed (lerping between two equal-length vectors otherwise shortens it).
                val desired = direction * speed
                val steered = lerp(arrow.velocity.toFloat3(), desired, 1 - SMOOTHNESS).normalize()

                arrow.velocity = (steered * speed).toVector()
                break
            }

        }.runTaskTimer(3L, CHECK_DELAY)
    }

    /**
     * Solves for the point at which a projectile of constant [speed] launched from
     * [shooter] will intercept a target currently at [targetPos] moving at [targetVel]
     * (all speeds/velocities in blocks per tick).
     *
     * Returns the predicted future position to aim at, or the target's current
     * position when no positive-time interception exists (e.g. the target is moving
     * away faster than the projectile can travel).
     */
    private fun predictIntercept(
        shooter: Float3,
        speed: Float,
        targetPos: Float3,
        targetVel: Float3
    ): Float3 {
        val rx = targetPos.x - shooter.x
        val ry = targetPos.y - shooter.y
        val rz = targetPos.z - shooter.z

        // |r + targetVel * t| = speed * t  ->  a t^2 + b t + c = 0
        val a = (targetVel.x * targetVel.x + targetVel.y * targetVel.y + targetVel.z * targetVel.z) - speed * speed
        val b = 2f * (rx * targetVel.x + ry * targetVel.y + rz * targetVel.z)
        val c = rx * rx + ry * ry + rz * rz

        var t = -1f

        if (abs(a) < 1e-4f) {
            // Target and projectile speeds match: equation is linear.
            if (abs(b) > 1e-4f) {
                t = -c / b
            }
        } else {
            val disc = b * b - 4f * a * c

            if (disc >= 0f) {
                val sqrtDisc = sqrt(disc.coerceAtLeast(0f))
                val t1 = (-b - sqrtDisc) / (2f * a)
                val t2 = (-b + sqrtDisc) / (2f * a)

                t = when {
                    t1 > 0f && t2 > 0f -> min(t1, t2)
                    t1 > 0f -> t1
                    t2 > 0f -> t2
                    else -> -1f
                }
            }
        }

        if (t <= 0f) {
            return targetPos
        }

        val lead = t.coerceAtMost(MAX_LEAD_TICKS)

        return Float3(
            targetPos.x + targetVel.x * lead,
            targetPos.y + targetVel.y * lead,
            targetPos.z + targetVel.z * lead
        )
    }

    override fun makeCompileData(config: Config, context: ViolationContext): List<TestableEntity> {
        return config.getStrings("targets").map {
            Entities.lookup(it)
        }
    }
}
