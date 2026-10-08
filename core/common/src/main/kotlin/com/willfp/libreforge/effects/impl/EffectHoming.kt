package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.entities.Entities
import com.willfp.eco.core.entities.TestableEntity
import com.willfp.eco.core.integrations.antigrief.AntigriefManager
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.ConfigWarning
import com.willfp.libreforge.Regions
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.enumValueOfOrNull
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.normalize
import com.willfp.libreforge.plugin
import com.willfp.libreforge.toFloat3
import com.willfp.libreforge.toVector
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import dev.romainguy.kotlin.math.Float3
import dev.romainguy.kotlin.math.dot
import dev.romainguy.kotlin.math.length
import org.bukkit.FluidCollisionMode
import org.bukkit.GameMode
import org.bukkit.entity.AbstractArrow
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.ProjectileLaunchEvent
import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt

object EffectHoming : Effect<EffectHoming.HomingCompileData>("homing") {
    override val description = "Makes fired arrows lock on to the target they are aimed at and curve towards it."
    override val categories = setOf("combat")

    override val arguments = arguments {
        require(
            "distance",
            "You must specify the distance to hone from!",
            description = "The maximum range at which the arrow will lock on to and follow a target. Supports expressions.",
            type = ArgType.EXPRESSION,
            example = "10 + %level%"
        )
        optional(
            "targets",
            description = "A list of entities the arrow will home in on. Leave empty to target all entities. In entity target mode this list is also the priority order, first entry highest.",
            type = ArgType.ENTITY_LIST,
            default = "[]"
        )
        optional(
            "target_change",
            description = "Whether the arrow may lock on to a new target after losing its current one.",
            type = ArgType.BOOLEAN,
            default = "false"
        )
        optional(
            "turn_rate",
            description = "The maximum number of degrees the arrow can turn each tick. Supports expressions.",
            type = ArgType.EXPRESSION,
            default = "15"
        )
        optional(
            "target_mode",
            description = "How the arrow picks between valid targets.",
            type = ArgType.STRING,
            default = "closest_to_crosshair",
            enumClass = HomingTargetMode::class
        )
        optional(
            "live_mode",
            description = "In closest_to_crosshair mode, whether the arrow keeps switching to whichever target the shooter is currently looking closest to.",
            type = ArgType.BOOLEAN,
            default = "false"
        )
        optional(
            "acquire_angle",
            description = "How many degrees off the arrow's heading a target can be and still be locked on to. Supports expressions.",
            type = ArgType.EXPRESSION,
            default = "30"
        )
    }

    override val parameters = setOf(
        TriggerParameter.PROJECTILE
    )

    private const val META_KEY_DISTANCE = "libreforge-homing-arrows-distance"
    private const val META_KEY_TARGETS = "libreforge-homing-arrows-targets"
    private const val META_KEY_TURN_RATE = "libreforge-homing-arrows-turn-rate"
    private const val META_KEY_ACQUIRE_ANGLE = "libreforge-homing-arrows-acquire-angle"
    private const val META_KEY_TARGET_MODE = "libreforge-homing-arrows-target-mode"
    private const val META_KEY_LIVE_MODE = "libreforge-homing-arrows-live-mode"
    private const val META_KEY_TARGET_CHANGE = "libreforge-homing-arrows-target-change"
    private const val META_KEY_TRACKED = "libreforge-homing-arrows-tracked"

    private const val DEFAULT_TURN_RATE = 15.0
    private const val DEFAULT_ACQUIRE_ANGLE = 30.0

    // Ticks after launch before homing kicks in, so the arrow clears the shooter first.
    private const val START_DELAY = 2L

    // Homing gives up after this many ticks, even if the arrow is still flying.
    private const val MAX_LIFETIME_TICKS = 100

    // How long a locked target may stay out of line of sight before the lock is dropped.
    private const val MAX_TICKS_OUT_OF_SIGHT = 10

    // Upper bound on how far ahead (in ticks) the intercept prediction may lead a target.
    private const val MAX_LEAD_TICKS = 20f

    private val EXCLUDED_TYPES = setOf(EntityType.ENDERMAN, EntityType.ARMOR_STAND)

    override fun onTrigger(config: Config, data: TriggerData, compileData: HomingCompileData): Boolean {
        val arrow = data.projectile as? AbstractArrow ?: return false

        var distance = config.getDoubleFromExpression("distance", data)

        if (distance < 0.5) {
            return false
        }

        var turnRate = if (config.has("turn_rate")) {
            config.getDoubleFromExpression("turn_rate", data)
        } else {
            DEFAULT_TURN_RATE
        }

        var acquireAngle = if (config.has("acquire_angle")) {
            config.getDoubleFromExpression("acquire_angle", data)
        } else {
            DEFAULT_ACQUIRE_ANGLE
        }

        var targetChange = config.getBoolOrNull("target_change") ?: false
        var liveMode = compileData.liveMode
        var targetMode = compileData.targetMode
        val targets = compileData.targets.toMutableList()

        // Multiple homing effects on one arrow stack: ranges add, the best turn rate and angle win,
        // and the first effect's target mode and priority order win.
        if (arrow.hasMetadata(META_KEY_DISTANCE)) {
            distance += arrow.getMetadata(META_KEY_DISTANCE).firstOrNull()?.value() as? Double ?: 0.0
            turnRate = maxOf(turnRate, arrow.getMetadata(META_KEY_TURN_RATE).firstOrNull()?.value() as? Double ?: 0.0)
            acquireAngle = maxOf(acquireAngle, arrow.getMetadata(META_KEY_ACQUIRE_ANGLE).firstOrNull()?.value() as? Double ?: 0.0)
            targetChange = targetChange || arrow.getMetadata(META_KEY_TARGET_CHANGE).firstOrNull()?.value() as? Boolean ?: false
            liveMode = liveMode || arrow.getMetadata(META_KEY_LIVE_MODE).firstOrNull()?.value() as? Boolean ?: false
            targetMode = arrow.getMetadata(META_KEY_TARGET_MODE).firstOrNull()?.value() as? HomingTargetMode ?: targetMode
            @Suppress("UNCHECKED_CAST")
            val existingTargets = arrow.getMetadata(META_KEY_TARGETS).firstOrNull()?.value() as? List<TestableEntity>
                ?: emptyList()
            targets.addAll(0, existingTargets)
        }

        arrow.setMetadata(META_KEY_DISTANCE, plugin.createMetadataValue(distance))
        arrow.setMetadata(META_KEY_TARGETS, plugin.createMetadataValue(targets))
        arrow.setMetadata(META_KEY_TURN_RATE, plugin.createMetadataValue(turnRate))
        arrow.setMetadata(META_KEY_ACQUIRE_ANGLE, plugin.createMetadataValue(acquireAngle))
        arrow.setMetadata(META_KEY_TARGET_CHANGE, plugin.createMetadataValue(targetChange))
        arrow.setMetadata(META_KEY_LIVE_MODE, plugin.createMetadataValue(liveMode))
        arrow.setMetadata(META_KEY_TARGET_MODE, plugin.createMetadataValue(targetMode))

        return true
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    fun handle(event: ProjectileLaunchEvent) {
        val arrow = event.entity as? AbstractArrow ?: return
        val shooter = arrow.shooter as? Player ?: return

        if (arrow.hasMetadata(META_KEY_TRACKED)) {
            return
        }

        val distance = arrow.getMetadata(META_KEY_DISTANCE).firstOrNull()?.value() as? Double ?: return

        @Suppress("UNCHECKED_CAST")
        val targets = arrow.getMetadata(META_KEY_TARGETS).firstOrNull()?.value() as? List<TestableEntity> ?: return
        val turnRate = arrow.getMetadata(META_KEY_TURN_RATE).firstOrNull()?.value() as? Double ?: DEFAULT_TURN_RATE
        val acquireAngle = arrow.getMetadata(META_KEY_ACQUIRE_ANGLE).firstOrNull()?.value() as? Double ?: DEFAULT_ACQUIRE_ANGLE
        val targetChange = arrow.getMetadata(META_KEY_TARGET_CHANGE).firstOrNull()?.value() as? Boolean ?: false
        val liveMode = arrow.getMetadata(META_KEY_LIVE_MODE).firstOrNull()?.value() as? Boolean ?: false
        val targetMode = arrow.getMetadata(META_KEY_TARGET_MODE).firstOrNull()?.value() as? HomingTargetMode
            ?: HomingTargetMode.CLOSEST_TO_CROSSHAIR

        arrow.setMetadata(META_KEY_TRACKED, plugin.createMetadataValue(true))

        val tracker = HomingTracker(
            arrow,
            shooter,
            distance,
            targets,
            Math.toRadians(turnRate).toFloat(),
            Math.toRadians(acquireAngle),
            targetChange,
            targetMode,
            liveMode && targetMode == HomingTargetMode.CLOSEST_TO_CROSSHAIR
        )

        plugin.scheduler.on(arrow).runTimer({ task ->
            if (!tracker.tick()) {
                task.cancel()
            }
        }, START_DELAY, 1L)
    }

    private class HomingTracker(
        private val arrow: AbstractArrow,
        private val shooter: Player,
        private val range: Double,
        private val targets: List<TestableEntity>,
        private val maxTurnRadians: Float,
        private val maxAcquireRadians: Double,
        private val targetChange: Boolean,
        private val targetMode: HomingTargetMode,
        private val liveMode: Boolean
    ) {
        private var target: LivingEntity? = null
        private var lastTargetPosition: Float3? = null
        private var ticksOutOfSight = 0
        private var hasLocked = false
        private var ticksAlive = 0

        /**
         * Runs one tick of homing. Returns false once homing is finished for this arrow.
         */
        fun tick(): Boolean {
            ticksAlive++

            if (ticksAlive > MAX_LIFETIME_TICKS || arrow.isDead || arrow.isInBlock || arrow.isOnGround) {
                return false
            }

            val arrowPosition = arrow.location.toFloat3()

            val lockedTarget = target ?: run {
                // Without target_change, an arrow that has already lost a lock flies on normally.
                if (hasLocked && !targetChange) {
                    return false
                }

                val acquired = findBestTarget(arrowPosition) ?: return true
                lockOn(acquired)
                acquired
            }

            val currentTarget = if (liveMode && shooter.isOnline && !shooter.isDead) {
                // Live mode: follow whichever target the shooter is looking closest to right now,
                // keeping the current lock when nothing better is in view.
                val preferred = findBestTarget(arrowPosition)

                if (preferred != null && preferred != lockedTarget) {
                    lockOn(preferred)
                    preferred
                } else {
                    lockedTarget
                }
            } else {
                lockedTarget
            }

            val targetPosition = currentTarget.boundingBox.center.toFloat3()

            if (!isLockStillValid(currentTarget, arrowPosition, targetPosition)) {
                target = null
                lastTargetPosition = null
                return targetChange
            }

            // Track the target's movement ourselves; server-side velocity is meaningless for players.
            val targetVelocity = lastTargetPosition?.let { targetPosition - it } ?: Float3()
            lastTargetPosition = targetPosition

            if (ticksOutOfSight > 0) {
                return true
            }

            steerTowards(arrowPosition, targetPosition, targetVelocity)
            return true
        }

        /**
         * Finds the best eligible target for the current [targetMode]. Eligible means valid,
         * within range, inside the acquire cone around the arrow's heading, and in line of sight.
         * Ties are broken by the smallest angle off the arrow's heading.
         */
        private fun findBestTarget(arrowPosition: Float3): LivingEntity? {
            val heading = arrow.velocity.toFloat3()

            if (length(heading) < 1e-3f) {
                return null
            }

            val headingDirection = heading.normalize()
            val rangeSquared = (range * range).toFloat()

            if (!Regions.canReach(arrow.location, range)) {
                return null
            }

            return arrow.getNearbyEntities(range, range, range)
                .asSequence()
                .filterIsInstance<LivingEntity>()
                .filter { isValidTarget(it) }
                .map { entity ->
                    val position = entity.boundingBox.center.toFloat3()
                    val offset = position - arrowPosition
                    TargetCandidate(
                        entity,
                        position,
                        dot(offset, offset),
                        angleBetween(headingDirection, offset.normalize())
                    )
                }
                .filter { it.distanceSquared <= rangeSquared }
                .filter { it.headingAngle <= maxAcquireRadians }
                .sortedWith(modeComparator().thenBy { it.headingAngle })
                .firstOrNull { hasLineOfSight(arrowPosition, it.position) }
                ?.entity
        }

        private fun modeComparator(): Comparator<TargetCandidate> {
            return when (targetMode) {
                HomingTargetMode.CLOSEST_TO_ARROW -> compareBy { it.distanceSquared }

                HomingTargetMode.CLOSEST_TO_CROSSHAIR -> {
                    if (!shooter.isOnline || shooter.isDead) {
                        compareBy { it.headingAngle }
                    } else {
                        val eyePosition = shooter.eyeLocation.toFloat3()
                        val lookDirection = shooter.eyeLocation.direction.toFloat3().normalize()
                        compareBy { angleBetween(lookDirection, (it.position - eyePosition).normalize()) }
                    }
                }

                HomingTargetMode.LOWEST_HEALTH -> compareBy { it.entity.health }

                HomingTargetMode.HIGHEST_HEALTH -> compareByDescending { it.entity.health }

                HomingTargetMode.ENTITY -> compareBy { candidate ->
                    val priority = targets.indexOfFirst { it.matches(candidate.entity) }
                    if (priority < 0) Int.MAX_VALUE else priority
                }
            }
        }

        private fun lockOn(newTarget: LivingEntity) {
            target = newTarget
            lastTargetPosition = null
            ticksOutOfSight = 0
            hasLocked = true
        }

        private fun isLockStillValid(candidate: LivingEntity, arrowPosition: Float3, targetPosition: Float3): Boolean {
            if (!candidate.isValid || candidate.world != arrow.world || !isValidTarget(candidate)) {
                return false
            }

            if (length(targetPosition - arrowPosition) > range) {
                return false
            }

            if (hasLineOfSight(arrowPosition, targetPosition)) {
                ticksOutOfSight = 0
            } else {
                ticksOutOfSight++
            }

            return ticksOutOfSight <= MAX_TICKS_OUT_OF_SIGHT
        }

        private fun isValidTarget(candidate: LivingEntity): Boolean {
            if (candidate.uniqueId == shooter.uniqueId || candidate.isDead || candidate.type in EXCLUDED_TYPES) {
                return false
            }

            if (candidate is Player && candidate.gameMode !in setOf(GameMode.ADVENTURE, GameMode.SURVIVAL)) {
                return false
            }

            if (targets.isNotEmpty() && targets.none { it.matches(candidate) }) {
                return false
            }

            return AntigriefManager.canInjure(shooter, candidate)
        }

        private fun hasLineOfSight(from: Float3, to: Float3): Boolean {
            val offset = to - from
            val distance = length(offset)

            if (distance < 1e-3f) {
                return true
            }

            return arrow.world.rayTraceBlocks(
                arrow.location,
                offset.normalize().toVector(),
                distance.toDouble(),
                FluidCollisionMode.NEVER,
                true
            ) == null
        }

        /**
         * Rotates the arrow's velocity towards the predicted intercept point by at most
         * [maxTurnRadians], leaving its speed untouched so drag and gravity behave as vanilla.
         */
        private fun steerTowards(arrowPosition: Float3, targetPosition: Float3, targetVelocity: Float3) {
            val velocity = arrow.velocity.toFloat3()
            val speed = length(velocity)

            if (speed < 1e-3f) {
                return
            }

            val aimPoint = predictIntercept(arrowPosition, speed, targetPosition, targetVelocity)
            val currentDirection = velocity / speed
            val desiredDirection = (aimPoint - arrowPosition).normalize()

            val newDirection = rotateTowards(currentDirection, desiredDirection, maxTurnRadians) ?: return

            arrow.velocity = (newDirection * speed).toVector()
        }
    }

    private class TargetCandidate(
        val entity: LivingEntity,
        val position: Float3,
        val distanceSquared: Float,
        val headingAngle: Double
    )

    enum class HomingTargetMode {
        CLOSEST_TO_ARROW,
        CLOSEST_TO_CROSSHAIR,
        LOWEST_HEALTH,
        HIGHEST_HEALTH,
        ENTITY
    }

    data class HomingCompileData(
        val targets: List<TestableEntity>,
        val targetMode: HomingTargetMode,
        val liveMode: Boolean
    )

    private fun angleBetween(first: Float3, second: Float3): Double {
        return acos(dot(first, second).coerceIn(-1f, 1f)).toDouble()
    }

    /**
     * Rotates unit vector [from] towards unit vector [to] by at most [maxRadians] along the
     * great circle between them. Returns null when they are directly opposite, as the
     * rotation plane is then undefined.
     */
    private fun rotateTowards(from: Float3, to: Float3, maxRadians: Float): Float3? {
        val angle = acos(dot(from, to).coerceIn(-1f, 1f))

        if (angle <= maxRadians) {
            return to
        }

        val sinAngle = sin(angle)

        if (sinAngle < 1e-4f) {
            return null
        }

        val fromWeight = sin(angle - maxRadians) / sinAngle
        val toWeight = sin(maxRadians) / sinAngle

        return (from * fromWeight + to * toWeight).normalize()
    }

    /**
     * Solves for the point at which a projectile of constant [projectileSpeed] launched from
     * [shooterPosition] will intercept a target currently at [targetPosition] moving at
     * [targetVelocity] (all speeds and velocities in blocks per tick).
     *
     * Returns the predicted future position to aim at, or the target's current position
     * when no positive-time interception exists.
     */
    private fun predictIntercept(
        shooterPosition: Float3,
        projectileSpeed: Float,
        targetPosition: Float3,
        targetVelocity: Float3
    ): Float3 {
        val relativePosition = targetPosition - shooterPosition

        // |relativePosition + targetVelocity * time| = projectileSpeed * time, as a quadratic in time.
        val quadraticTerm = dot(targetVelocity, targetVelocity) - projectileSpeed * projectileSpeed
        val linearTerm = 2f * dot(relativePosition, targetVelocity)
        val constantTerm = dot(relativePosition, relativePosition)

        var interceptTime = -1f

        if (abs(quadraticTerm) < 1e-4f) {
            // Target and projectile speeds match, so the equation is linear.
            if (abs(linearTerm) > 1e-4f) {
                interceptTime = -constantTerm / linearTerm
            }
        } else {
            val discriminant = linearTerm * linearTerm - 4f * quadraticTerm * constantTerm

            if (discriminant >= 0f) {
                val discriminantRoot = sqrt(discriminant)
                val earlierTime = (-linearTerm - discriminantRoot) / (2f * quadraticTerm)
                val laterTime = (-linearTerm + discriminantRoot) / (2f * quadraticTerm)

                interceptTime = when {
                    earlierTime > 0f && laterTime > 0f -> min(earlierTime, laterTime)
                    earlierTime > 0f -> earlierTime
                    laterTime > 0f -> laterTime
                    else -> -1f
                }
            }
        }

        if (interceptTime <= 0f) {
            return targetPosition
        }

        return targetPosition + targetVelocity * interceptTime.coerceAtMost(MAX_LEAD_TICKS)
    }

    override fun makeCompileData(config: Config, context: ViolationContext): HomingCompileData {
        val targets = config.getStrings("targets").map {
            Entities.lookup(it)
        }

        val rawMode = config.getStringOrNull("target_mode")
        val targetMode = if (rawMode == null) {
            HomingTargetMode.CLOSEST_TO_CROSSHAIR
        } else {
            enumValueOfOrNull<HomingTargetMode>(rawMode.uppercase().replace('-', '_').replace(' ', '_')) ?: run {
                context.log(
                    ConfigViolation(
                        "target_mode",
                        "Invalid target mode '$rawMode'! Valid modes: ${HomingTargetMode.entries.joinToString { it.name.lowercase() }}"
                    )
                )
                HomingTargetMode.CLOSEST_TO_CROSSHAIR
            }
        }

        if (targetMode == HomingTargetMode.ENTITY && targets.isEmpty()) {
            context.log(
                ConfigViolation(
                    "targets",
                    "The entity target mode requires a list of targets in priority order!"
                )
            )
        }

        val liveMode = config.getBoolOrNull("live_mode") ?: false

        if (liveMode && targetMode != HomingTargetMode.CLOSEST_TO_CROSSHAIR) {
            context.log(
                ConfigWarning(
                    "live_mode",
                    "live_mode only applies to the closest_to_crosshair target mode and will be ignored."
                )
            )
        }

        return HomingCompileData(targets, targetMode, liveMode)
    }
}
