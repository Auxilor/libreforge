package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.getIntFromExpression
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.entity.LivingEntity

object EffectChainLightning : Effect<NoCompileData>("chain_lightning") {
    override val description = "Strikes lightning that chains to nearby entities, dealing damage at each jump."
    override val categories = setOf("combat", "visual")

    override val parameters = setOf(
        TriggerParameter.VICTIM,
        TriggerParameter.LOCATION
    )

    override val arguments = arguments {
        require(
            "jumps",
            "You must specify the number of jumps!",
            description = "The number of entities the lightning can chain to. Supports expressions.",
            type = ArgType.EXPRESSION,
            example = "3 + %level%"
        )
        require(
            "range",
            "You must specify the chain range!",
            description = "The maximum distance between chain targets. Supports expressions.",
            type = ArgType.EXPRESSION,
            example = "5"
        )
        require(
            "damage",
            "You must specify the damage per jump!",
            description = "The damage dealt to each entity struck. Supports expressions.",
            type = ArgType.EXPRESSION,
            example = "%level% * 1.5"
        )
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val startVictim = data.victim ?: return false
        val jumps = config.getIntFromExpression("jumps", data)
        val range = config.getDoubleFromExpression("range", data)
        val damage = config.getDoubleFromExpression("damage", data)
        val player = data.player

        val hit = mutableSetOf<LivingEntity>()
        var current = startVictim

        repeat(jumps) {
            hit.add(current)
            current.world.strikeLightningEffect(current.location)
            current.damage(damage)

            val next = current.getNearbyEntities(range, range, range)
                .filterIsInstance<LivingEntity>()
                .filter { it !in hit && it != player }
                .minByOrNull { it.location.distanceSquared(current.location) }
                ?: return true

            current = next
        }

        return true
    }
}
