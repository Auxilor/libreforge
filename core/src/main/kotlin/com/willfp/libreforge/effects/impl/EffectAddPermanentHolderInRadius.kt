package com.willfp.libreforge.effects.impl

import com.github.benmanes.caffeine.cache.Caffeine
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.Holder
import com.willfp.libreforge.HolderTemplate
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.SimpleProvidedHolder
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Conditions
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.effects.Effects
import com.willfp.libreforge.effects.Identifiers
import com.willfp.libreforge.get
import com.willfp.libreforge.nest
import com.willfp.libreforge.registerGenericHolderProvider
import org.bukkit.Bukkit
import java.util.Objects
import java.util.UUID
import java.util.concurrent.TimeUnit

object EffectAddPermanentHolderInRadius : Effect<HolderTemplate>("add_permanent_holder_in_radius") {
    override val arguments = arguments {
        require("effects", "You must specify the effects!")
        require("radius", "You must specify the radius!")
    }

    private val holders = mutableSetOf<PermanentNearbyHolder>()

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

    override fun onEnable(
        dispatcher: Dispatcher<*>,
        config: Config,
        identifiers: Identifiers,
        holder: ProvidedHolder,
        compileData: HolderTemplate
    ) {
        val radius = config.getDoubleFromExpression("radius", dispatcher.get())
        val applyToSelf = config.getBool("apply-to-self")

        val nearbyHolder = PermanentNearbyHolder(
            compileData.toHolder(identifiers.key).nest(holder),
            radius,
            dispatcher.uuid,
            applyToSelf
        )

        holders += nearbyHolder
    }

    override fun onDisable(dispatcher: Dispatcher<*>, identifiers: Identifiers, holder: ProvidedHolder) {
        holders.removeIf { it.holder.id == identifiers.key }
    }

    override fun makeCompileData(config: Config, context: ViolationContext): HolderTemplate {
        val effects = Effects.compile(
            config.getSubsections("effects"),
            context.with("add_permanent_holder_in_radius effects")
        )

        val conditions = Conditions.compile(
            config.getSubsections("conditions"),
            context.with("add_permanent_holder_in_radius conditions")
        )

        return HolderTemplate(
            effects,
            conditions
        )
    }

    private data class PermanentNearbyHolder(
        val holder: Holder,
        val radius: Double,
        val owner: UUID,
        val applyToSelf: Boolean
    ) {
        fun canApplyTo(dispatcher: Dispatcher<*>): Boolean {
            val dispatcherLocation = dispatcher.location ?: return false
            val location = Bukkit.getPlayer(owner)?.location ?: return false

            if (location.world != dispatcherLocation.world) {
                return false
            }

            if (dispatcher.uuid == owner && !applyToSelf) {
                return false
            }

            if (dispatcherLocation.toVector().distanceSquared(location.toVector()) > radius * radius) {
                return false
            }

            return true
        }

        override fun equals(other: Any?): Boolean {
            if (other !is PermanentNearbyHolder) {
                return false
            }

            return this.holder.id == other.holder.id
        }

        override fun hashCode(): Int {
            return Objects.hash(this.holder.id)
        }
    }
}
