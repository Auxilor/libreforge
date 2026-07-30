package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.dealDamage
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter

object EffectSmite : Effect<NoCompileData>("smite") {
    override val description = "Strikes the victim with lightning, dealing a configurable amount of damage."
    override val categories = setOf("combat", "world")

    override val parameters = setOf(
        TriggerParameter.VICTIM
    )

    override val arguments = arguments {
        require(
            "damage",
            "You must specify the damage to deal!",
            description = "The amount of damage the lightning strike deals to the victim. Supports expressions.",
            type = ArgType.EXPRESSION,
            example = "%level% * 2"
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
        val victim = data.victim ?: return false
        val damage = config.getDoubleFromExpression("damage", data.player)
        val useSource = config.getBool("use_source")

        victim.world.strikeLightningEffect(victim.location)
        victim.dealDamage(
            damage,
            if (useSource) data.player else null,
            config.getBool("true_damage")
        )

        return true
    }
}
