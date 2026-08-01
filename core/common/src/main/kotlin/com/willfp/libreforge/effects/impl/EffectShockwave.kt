package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.dealDamage
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.getIntFromExpression
import com.willfp.libreforge.plugin
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.entity.LivingEntity

object EffectShockwave : Effect<NoCompileData>("shockwave") {
    override val description = "Creates an expanding shockwave that knocks back and damages nearby entities."
    override val categories = setOf("combat", "movement")

    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        require(
            "radius",
            "You must specify the shockwave radius!",
            description = "The maximum radius the shockwave expands to. Supports expressions.",
            type = ArgType.EXPRESSION,
            example = "5 + %level% * 0.5"
        )
        require(
            "pulses",
            "You must specify the number of pulses!",
            description = "How many pulses the shockwave expands over before reaching its full radius. Supports expressions.",
            type = ArgType.EXPRESSION
        )
        require(
            "damage",
            "You must specify the damage per entity!",
            description = "The amount of damage dealt to each entity caught in the shockwave. Supports expressions.",
            type = ArgType.EXPRESSION,
            example = "%level% * 2"
        )
        require(
            "knockback",
            "You must specify the knockback force!",
            description = "The knockback force applied to each entity hit. Supports expressions.",
            type = ArgType.EXPRESSION,
            example = "1 + %level% * 0.1"
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
        val origin = player.location.clone()
        val radius = config.getDoubleFromExpression("radius", data)
        val pulses = config.getIntFromExpression("pulses", data)
        val damage = config.getDoubleFromExpression("damage", data)
        val knockback = config.getDoubleFromExpression("knockback", data)
        val trueDamage = config.getBool("true_damage")
        val source = if (config.getBool("use_source")) player else null

        val hit = mutableSetOf<LivingEntity>()
        var pulse = 0

        plugin.runnableFactory.create { task ->
            pulse++
            val currentRadius = radius * pulse / pulses

            origin.world?.getNearbyEntities(origin, currentRadius, currentRadius, currentRadius)
                ?.filterIsInstance<LivingEntity>()
                ?.filter { it !in hit && it != player }
                ?.forEach { entity ->
                    hit.add(entity)
                    val dir = entity.location.toVector()
                        .subtract(origin.toVector())
                        .normalize()
                    entity.velocity = dir.multiply(knockback)
                    entity.dealDamage(damage, source, trueDamage)
                }

            if (pulse >= pulses) task.cancel()
        }.runTaskTimer(0L, 3L)

        return true
    }
}
