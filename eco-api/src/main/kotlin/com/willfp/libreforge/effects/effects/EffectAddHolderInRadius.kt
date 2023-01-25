package com.willfp.libreforge.effects.effects

import com.github.benmanes.caffeine.cache.Caffeine
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.Holder
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Conditions
import com.willfp.libreforge.conditions.ConfiguredCondition
import com.willfp.libreforge.effects.CompileData
import com.willfp.libreforge.effects.ConfiguredEffect
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.effects.Effects
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.getIntFromExpression
import com.willfp.libreforge.triggers.InvocationData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.Triggers
import org.bukkit.Location
import java.util.Objects
import java.util.UUID
import java.util.concurrent.TimeUnit

class EffectAddHolderInRadius : Effect(
    "add_holder_in_radius",
    triggers = Triggers.withParameters(
        TriggerParameter.PLAYER
    )
) {
    override val arguments = arguments {
        require("effects", "You must specify the effects!")
        require("conditions", "You must specify the conditions!")
        require("duration", "You must specify the duration (in ticks)!")
        require("radius", "You must specify the radius!")
    }

    private val holders = mutableSetOf<NearbyHolder>()
    private val nearbyCache = Caffeine.newBuilder()
        .expireAfterWrite(250L, TimeUnit.MILLISECONDS)
        .build<UUID, Iterable<Holder>>()

    init {
        plugin.registerHolderProvider { player ->
            nearbyCache.get(player.uniqueId) {
                val found = mutableSetOf<Holder>()

                for ((holder, loc, rad, uuid, applyToSelf) in holders) {
                    if (loc.world != player.world) {
                        continue
                    }

                    if (uuid == player.uniqueId && !applyToSelf) {
                        continue
                    }

                    if (loc.toVector().distanceSquared(player.location.toVector()) < rad * rad) {
                        found.add(holder)
                    }
                }

                found
            }
        }
    }

    override fun handle(invocation: InvocationData, config: Config) {
        val player = invocation.data.player ?: return
        val location = invocation.data.location ?: return
        val unfinished = invocation.compileData as? UnfinishedHolder ?: return

        val uuid = UUID.randomUUID()

        val radius = config.getDoubleFromExpression("radius", invocation.data)
        val duration = config.getIntFromExpression("duration", invocation.data)
        val applyToSelf = config.getBool("apply-to-self")

        val holder = AddedHolder(
            unfinished.effects,
            unfinished.conditions,
            "add_holder_in_radius:$uuid"
        )

        val nearby = NearbyHolder(
            holder,
            location,
            radius,
            player.uniqueId,
            applyToSelf
        )

        holders.add(nearby)

        plugin.scheduler.runLater(duration.toLong()) {
            holders.remove(nearby)
        }
    }

    override fun makeCompileData(config: Config, context: ViolationContext): CompileData {
        val effects = Effects.compile(
            config.getSubsections("effects"),
            context.with("add_holder_in_radius Effects")
        )

        val conditions = Conditions.compile(
            config.getSubsections("conditions"),
            context.with("add_holder_in_radius Conditions")
        )

        return UnfinishedHolder(
            effects,
            conditions
        )
    }

    private data class UnfinishedHolder(
        val effects: List<ConfiguredEffect>,
        val conditions: List<ConfiguredCondition>
    ) : CompileData


    private class AddedHolder(
        override val effects: List<ConfiguredEffect>,
        override val conditions: List<ConfiguredCondition>,
        override val id: String
    ) : Holder

    private data class NearbyHolder(
        val holder: Holder,
        val location: Location,
        val radius: Double,
        val uuid: UUID,
        val applyToSelf: Boolean
    ) {
        override fun equals(other: Any?): Boolean {
            if (other !is NearbyHolder) {
                return false
            }

            return this.holder.id == other.holder.id &&
                    this.location == other.location
        }

        override fun hashCode(): Int {
            return Objects.hashCode(this.holder.id)
        }
    }
}
