package com.willfp.libreforge.effects.impl

import com.github.benmanes.caffeine.cache.Caffeine
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.EmptyProvidedHolder.holder
import com.willfp.libreforge.GlobalDispatcher.location
import com.willfp.libreforge.Holder
import com.willfp.libreforge.HolderTemplate
import com.willfp.libreforge.SimpleProvidedHolder
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Conditions
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.effects.Effects
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.getIntFromExpression
import com.willfp.libreforge.nest
import com.willfp.libreforge.plugin
import com.willfp.libreforge.registerGenericHolderProvider
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.Location
import java.util.Objects
import java.util.UUID
import java.util.concurrent.TimeUnit

object EffectAddHolderInRadius : Effect<HolderTemplate>("add_holder_in_radius") {
    override val isPermanent = false

    override val arguments = arguments {
        require("effects", "You must specify the effects!")
        require("duration", "You must specify the duration (in ticks)!")
        require("radius", "You must specify the radius!")
    }

    private val holders = mutableSetOf<NearbyHolder>()

    private val nearbyCache = Caffeine.newBuilder()
        .expireAfterWrite(250L, TimeUnit.MILLISECONDS)
        .build<UUID, Collection<SimpleProvidedHolder>>()

    init {
        registerGenericHolderProvider { dispatcher ->
            nearbyCache.get(dispatcher.uuid) { _ ->
                holders.filter { it.canApplyTo(dispatcher) }
                    .map { SimpleProvidedHolder(it.holder) }
            }
        }
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: HolderTemplate): Boolean {
        val dispatcher = data.dispatcher
        val location = dispatcher.location ?: return false

        val radius = config.getDoubleFromExpression("radius", data)
        val duration = config.getIntFromExpression("duration", data)
        val applyToSelf = config.getBool("apply-to-self")

        val holder = NearbyHolder(
            compileData.toHolder().nest(data.holder),
            location,
            radius,
            dispatcher.uuid,
            applyToSelf
        )

        holders += holder
        plugin.scheduler.runLater(duration.toLong()) {
            holders -= holder
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
            val location = dispatcher.location ?: return false

            if (location.world != location.world) {
                return false
            }

            if (dispatcher.uuid == uuid && !applyToSelf) {
                return false
            }

            if (location.toVector().distanceSquared(location.toVector()) > radius * radius) {
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
