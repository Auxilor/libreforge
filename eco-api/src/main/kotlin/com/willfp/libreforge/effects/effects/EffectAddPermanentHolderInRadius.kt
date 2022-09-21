package com.willfp.libreforge.effects.effects

import com.github.benmanes.caffeine.cache.Caffeine
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.Holder
import com.willfp.libreforge.conditions.Conditions
import com.willfp.libreforge.conditions.ConfiguredCondition
import com.willfp.libreforge.effects.CompileData
import com.willfp.libreforge.effects.ConfiguredEffect
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.effects.Effects
import com.willfp.libreforge.effects.Identifiers
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.Triggers
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.Objects
import java.util.UUID
import java.util.concurrent.TimeUnit

class EffectAddPermanentHolderInRadius : Effect(
    "add_permanent_holder_in_radius",
    triggers = Triggers.withParameters(
        TriggerParameter.PLAYER
    )
) {
    private val holders = mutableMapOf<UUID, NearbyHolder>()
    private val nearbyCache = Caffeine.newBuilder()
        .expireAfterWrite(250L, TimeUnit.MILLISECONDS)
        .build<UUID, Iterable<Holder>>()

    init {
        plugin.registerHolderProvider { player ->
            nearbyCache.get(player.uniqueId) {
                val found = mutableSetOf<Holder>()

                for ((holder, owner, rad, applyToSelf) in holders.values) {
                    if (owner == null) {
                        continue
                    }

                    if (owner.world != player.world) {
                        continue
                    }

                    if (owner == player && !applyToSelf) {
                        continue
                    }

                    if (owner.location.toVector().distanceSquared(player.location.toVector()) < rad * rad) {
                        found.add(holder)
                    }
                }

                found
            }
        }
    }

    override fun handleEnable(player: Player, config: Config, identifiers: Identifiers, compileData: CompileData?) {
        val data = compileData as? HolderCompileData ?: return
        val unfinished = data.data

        val uuid = identifiers.uuid

        val radius = config.getDoubleFromExpression("radius", player)
        val applyToSelf = config.getBool("apply-to-self")

        val holder = AddedHolder(
            unfinished.effects,
            unfinished.conditions,
            "add_permanent_holder_in_radius:$uuid"
        )

        val nearby = NearbyHolder(
            holder,
            player.uniqueId,
            radius,
            applyToSelf
        )

        holders[uuid] = nearby
    }

    override fun handleDisable(player: Player, identifiers: Identifiers) {
        holders.remove(identifiers.uuid)
    }

    override fun makeCompileData(config: Config, context: String): CompileData {
        val effects = config.getSubsections("effects").mapNotNull {
            Effects.compile(it, "$context -> add_permanent_holder_in_radius Effects")
        }.toSet()

        val conditions = config.getSubsections("conditions").mapNotNull {
            Conditions.compile(it, "$context -> add_permanent_holder_in_radius Conditions")
        }.toSet()

        return HolderCompileData(
            HolderCompileData.UnfinishedHolder(
                effects,
                conditions
            )
        )
    }

    override fun validateConfig(config: Config): List<ConfigViolation> {
        val violations = mutableListOf<ConfigViolation>()

        if (!config.has("effects")) violations.add(
            ConfigViolation(
                "effects",
                "You must specify the effects!"
            )
        )

        if (!config.has("conditions")) violations.add(
            ConfigViolation(
                "conditions",
                "You must specify the conditions!"
            )
        )

        if (!config.has("radius")) violations.add(
            ConfigViolation(
                "radius",
                "You must specify the radius (in ticks)!"
            )
        )

        return violations
    }

    private class HolderCompileData(
        override val data: UnfinishedHolder
    ) : CompileData {
        data class UnfinishedHolder(
            val effects: Set<ConfiguredEffect>,
            val conditions: Set<ConfiguredCondition>
        )
    }

    private class AddedHolder(
        override val effects: Set<ConfiguredEffect>,
        override val conditions: Set<ConfiguredCondition>,
        override val id: String
    ) : Holder

    private class NearbyHolder(
        val holder: Holder,
        private val uuid: UUID,
        val radius: Double,
        val applyToSelf: Boolean
    ) {
        val owner: Player?
            get() = Bukkit.getPlayer(uuid)

        // Why not a data class? UUID -> Player mapping.
        operator fun component1(): Holder = holder
        operator fun component2(): Player? = owner
        operator fun component3(): Double = radius
        operator fun component4(): Boolean = applyToSelf

        override fun equals(other: Any?): Boolean {
            if (other !is NearbyHolder) {
                return false
            }

            return this.holder.id == other.holder.id &&
                    this.uuid == other.uuid
        }

        override fun hashCode(): Int {
            return Objects.hashCode(this.holder.id)
        }
    }
}
