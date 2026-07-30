package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.dealDamage
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter


object EffectDamageVictim : Effect<NoCompileData>("damage_victim") {
    override val description = "Deals damage to the victim."
    override val categories = setOf("combat")

    override val parameters = setOf(
        TriggerParameter.VICTIM
    )

    override val arguments = arguments {
        require(
            "damage",
            "You must specify the amount of damage!",
            description = "The amount of damage to deal. Supports expressions.",
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
        val player = data.player

        val damage = config.getDoubleFromExpression("damage", data)
        val useSource = config.getBool("use_source")

        victim.dealDamage(
            damage,
            source = if (useSource) player else null,
            trueDamage = config.getBool("true_damage"),
            checkAntigrief = false
        )

        return true
    }
}
