package com.willfp.libreforge.effects.impl

import com.github.benmanes.caffeine.cache.Caffeine
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.Holder
import com.willfp.libreforge.HolderTemplate
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Conditions
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.effects.Effects
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.getIntFromExpression
import com.willfp.libreforge.plugin
import com.willfp.libreforge.registerHolderProvider
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.Objects
import java.util.UUID
import java.util.concurrent.TimeUnit

object EffectAddHolderInRadius : Effect<HolderTemplate>("add_holder_in_radius") {
    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        require("effects", "You must specify the effects!")
        require("conditions", "You must specify the conditions!")
        require("duration", "You must specify the duration (in ticks)!")
        require("radius", "You must specify the radius!")
    }

    private val holders = mutableSetOf<NearbyHolder>()

    private val nearbyCache = Caffeine.newBuilder()
        .expireAfterWrite(250L, TimeUnit.MILLISECONDS)
        .build<UUID, Collection<Holder>>()

    init {
        registerHolderProvider { player ->
            nearbyCache.get(player.uniqueId) { _ ->
                holders.filter { it.canApplyTo(player) }
                    .map { it.holder }
            }
        }
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: HolderTemplate): Boolean {
        val player = data.player ?: return false
        val location = data.location ?: return false

        val radius = config.getDoubleFromExpression("radius", data)
        val duration = config.getIntFromExpression("duration", data)
        val applyToSelf = config.getBool("apply-to-self")

        val holder = NearbyHolder(
            compileData.toHolder(),
            location,
            radius,
            player.uniqueId,
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
            context.with("add_holder_in_radius Effects")
        )

        val conditions = Conditions.compile(
            config.getSubsections("conditions"),
            context.with("add_holder_in_radius Conditions")
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
        fun canApplyTo(player: Player): Boolean {
            if (location.world != player.world) {
                return false
            }

            if (player.uniqueId == uuid && !applyToSelf) {
                return false
            }

            if (player.location.toVector().distanceSquared(location.toVector()) > radius * radius) {
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
