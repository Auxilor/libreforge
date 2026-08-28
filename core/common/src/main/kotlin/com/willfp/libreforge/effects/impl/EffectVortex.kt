package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.entities.Entities
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.dealDamage
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.getFormattedStrings
import com.willfp.libreforge.getIntFromExpression
import com.willfp.libreforge.getOrNull
import com.willfp.libreforge.plugin
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.entity.LivingEntity

object EffectVortex : Effect<NoCompileData>("vortex") {
    override val description = "Pulls nearby entities toward the trigger location for a duration, then deals damage to all affected entities."
    override val categories = setOf("movement", "combat")

    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        require(
            "radius",
            "You must specify the radius!",
            description = "The radius within which entities will be pulled toward the vortex. Supports expressions.",
            type = ArgType.EXPRESSION,
            example = "5 + %level% * 0.5"
        )
        require(
            "duration",
            "You must specify the pull duration in ticks!",
            description = "How many ticks to pull entities before dealing damage. Supports expressions.",
            type = ArgType.EXPRESSION,
            example = "20 * %level%"
        )
        require(
            "damage",
            "You must specify the damage dealt at the end!",
            description = "The damage dealt to all affected entities at the end of the vortex. Supports expressions.",
            type = ArgType.EXPRESSION,
            example = "%level% * 2"
        )
        optional(
            "pull_strength",
            description = "How strongly entities are pulled per tick. Supports expressions.",
            type = ArgType.EXPRESSION,
            default = "0.3",
            example = "0.1 + %level% * 0.02"
        )
        optional(
            "whitelist",
            description = "A list of entity types to exclusively target. If omitted, all entities are targeted.",
            type = ArgType.ENTITY_LIST,
            default = ""
        )
        optional(
            "blacklist",
            description = "A list of entity types to exclude from the vortex.",
            type = ArgType.ENTITY_LIST,
            default = ""
        )
        optional(
            "true_damage",
            description = "If true, damage bypasses armor and resistance effects.",
            type = ArgType.BOOLEAN,
            default = "false"
        )
        optional(
            "use_source",
            description = "If true, the player is attributed as the damage source.",
            type = ArgType.BOOLEAN,
            default = "false"
        )
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val player = data.player ?: return false
        val origin = data.location?.clone() ?: return false
        val radius = config.getDoubleFromExpression("radius", data)
        val duration = config.getIntFromExpression("duration", data)
        val damage = config.getDoubleFromExpression("damage", data)
        val pullStrength = config.getOrNull("pull_strength") { getDoubleFromExpression(it, data) } ?: 0.3
        val trueDamage = config.getBool("true_damage")
        val source = if (config.getBool("use_source")) player else null

        val whitelist = config.getStringsOrNull("whitelist")?.map { Entities.lookup(it) }
        val blacklist = config.getFormattedStrings("blacklist", data).map { Entities.lookup(it) }

        val affected = mutableSetOf<LivingEntity>()
        var tick = 0

        plugin.runnableFactory.create { task ->
            tick++

            origin.world?.getNearbyEntities(origin, radius, radius, radius)
                ?.asSequence()
                ?.filterIsInstance<LivingEntity>()
                ?.filter { it != player }
                ?.filterNot { it.hasMetadata("NPC") }
                ?.filterNot { entity -> blacklist.any { it.matches(entity) } }
                ?.filter { entity -> whitelist == null || whitelist.any { it.matches(entity) } }
                ?.forEach { entity ->
                    affected.add(entity)
                    val pull = origin.toVector()
                        .subtract(entity.location.toVector())
                        .normalize()
                        .multiply(pullStrength)
                    entity.velocity = entity.velocity.add(pull)
                }

            if (tick >= duration) {
                affected.forEach { it.dealDamage(damage, source, trueDamage) }
                task.cancel()
            }
        }.runTaskTimer(0L, 1L)

        return true
    }
}
