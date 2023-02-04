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
import com.willfp.libreforge.effects.Identifiers
import com.willfp.libreforge.registerHolderProvider
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.Objects
import java.util.UUID
import java.util.concurrent.TimeUnit

object EffectAddPermanentHolderInRadius : Effect<HolderTemplate>("add_permanent_holder_in_radius") {
    override val arguments = arguments {
        require("effects", "You must specify the effects!")
        require("conditions", "You must specify the conditions!")
        require("radius", "You must specify the radius!")
    }

    private val holders = mutableSetOf<PermanentNearbyHolder>()

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

    override fun onEnable(player: Player, config: Config, identifiers: Identifiers, compileData: HolderTemplate) {
        val radius = config.getDoubleFromExpression("radius", player)
        val applyToSelf = config.getBool("apply-to-self")

        val holder = PermanentNearbyHolder(
            compileData.toHolder(identifiers.key),
            radius,
            player.uniqueId,
            applyToSelf
        )

        holders += holder
    }

    override fun onDisable(player: Player, identifiers: Identifiers) {
        holders.removeIf { it.holder.id == identifiers.key }
    }

    override fun makeCompileData(config: Config, context: ViolationContext): HolderTemplate {
        val effects = Effects.compile(
            config.getSubsections("effects"),
            context.with("add_permanent_holder_in_radius Effects")
        )

        val conditions = Conditions.compile(
            config.getSubsections("conditions"),
            context.with("add_permanent_holder_in_radius Conditions")
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
        fun canApplyTo(player: Player): Boolean {
            val location = Bukkit.getPlayer(owner)?.location ?: return false

            if (location.world != player.world) {
                return false
            }

            if (player.uniqueId == owner && !applyToSelf) {
                return false
            }

            if (player.location.toVector().distanceSquared(location.toVector()) > radius * radius) {
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
