package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.Holder
import com.willfp.libreforge.HolderChange
import com.willfp.libreforge.HolderPolling
import com.willfp.libreforge.HolderProvider
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.HolderTemplate
import com.willfp.libreforge.SimpleProvidedHolder
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Conditions
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.effects.Effects
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.getIntFromExpression
import com.willfp.libreforge.invalidateEverywhere
import com.willfp.libreforge.isPolledAsEntity
import com.willfp.libreforge.nest
import com.willfp.libreforge.plugin
import com.willfp.libreforge.registerHolderProvider
import com.willfp.libreforge.triggers.TriggerData
import org.bukkit.Location
import java.util.Objects
import java.util.UUID

object EffectAddHolderInRadius : Effect<HolderTemplate>("add_holder_in_radius") {
    override val description = "Temporarily applies a set of effects and conditions to all nearby entities within a radius."
    override val categories = setOf("meta")

    override val isPermanent = false

    override val arguments = arguments {
        require(
            "effects",
            "You must specify the effects!",
            description = "The effects to apply temporarily to nearby entities.",
            type = ArgType.EFFECT_LIST
        )
        require(
            "duration",
            "You must specify the duration (in ticks)!",
            description = "How long to apply the holder, in ticks. Supports expressions.",
            type = ArgType.EXPRESSION,
            example = "20 * %level%"
        )
        require(
            "radius",
            "You must specify the radius!",
            description = "The radius to apply effects within. Supports expressions.",
            type = ArgType.EXPRESSION,
            example = "5 + %level% * 0.5"
        )
        optional(
            "apply-to-self",
            description = "Whether to also apply the effects to the dispatcher.",
            type = ArgType.BOOLEAN,
            default = "false"
        )
        optional(
            "conditions",
            description = "The conditions the holder requires to be active.",
            type = ArgType.CONDITION_LIST
        )
    }

    private val holders = mutableSetOf<NearbyHolder>()

    // Invalidated everywhere when a holder is added or expires; polling covers movement.
    private val provider = object : HolderProvider {
        override val id = "libreforge:add_holder_in_radius"

        override val invalidatedBy = emptySet<HolderChange>()

        override fun maxAge(dispatcher: Dispatcher<*>): Int =
            if (dispatcher.isPolledAsEntity) HolderPolling.defaultMaxAge(dispatcher) else 20

        override fun provide(dispatcher: Dispatcher<*>): Collection<ProvidedHolder> {
            if (holders.isEmpty()) return emptyList()

            return holders.filter { it.canApplyTo(dispatcher) }
                .map { SimpleProvidedHolder(it.holder) }
        }
    }

    init {
        registerHolderProvider(provider)
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: HolderTemplate): Boolean {
        val dispatcher = data.dispatcher
        val location = dispatcher.location ?: return false

        val radius = config.getDoubleFromExpression("radius", data)
        val duration = config.getIntFromExpression("duration", data).coerceAtLeast(1)
        val applyToSelf = config.getBool("apply-to-self")

        val holder = NearbyHolder(
            compileData.toHolder().nest(data.holder),
            location,
            radius,
            dispatcher.uuid,
            applyToSelf
        )

        holders += holder
        provider.invalidateEverywhere()

        plugin.scheduler.runLater(duration.toLong()) {
            holders -= holder
            provider.invalidateEverywhere()
        }

        return true
    }

    override fun makeCompileData(config: Config, context: ViolationContext): HolderTemplate {
        val effects = Effects.compile(
            config.getSubsections("effects"),
            context.with("add_holder_in_radius effects")
        )

        val conditions = Conditions.compile(
            config.getSubsections("conditions"),
            context.with("add_holder_in_radius conditions")
        )

        return HolderTemplate(
            effects,
            conditions
        )
    }

    private data class NearbyHolder(
        val holder: Holder,
        val location: Location,
        val radius: Double,
        val uuid: UUID,
        val applyToSelf: Boolean
    ) {
        fun canApplyTo(dispatcher: Dispatcher<*>): Boolean {
            val otherLocation = dispatcher.location ?: return false

            if (location.world != otherLocation.world) {
                return false
            }

            if (dispatcher.uuid == uuid && !applyToSelf) {
                return false
            }

            if (location.toVector().distanceSquared(otherLocation.toVector()) > radius * radius) {
                return false
            }

            return true
        }

        override fun equals(other: Any?): Boolean {
            if (other !is NearbyHolder) {
                return false
            }

            return this.holder.id == other.holder.id &&
                    this.location == other.location
        }

        override fun hashCode(): Int {
            return Objects.hash(this.holder.id, this.location)
        }
    }
}
