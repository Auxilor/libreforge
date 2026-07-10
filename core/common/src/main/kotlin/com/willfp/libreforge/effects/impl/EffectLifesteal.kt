package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.attribute.Attribute

object EffectLifesteal : Effect<NoCompileData>("lifesteal") {
    override val description = "Heals the player for a portion of the damage dealt to the victim."
    override val categories = setOf("combat", "player")

    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        require(
            "multiplier",
            "You must specify the heal multiplier!",
            description = "The fraction of damage dealt that is converted into healing. Supports expressions.",
            type = ArgType.EXPRESSION,
            example = "0.2"
        )
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val player = data.player ?: return false
        val heal = data.value * config.getDoubleFromExpression("multiplier", data)
        player.health = (player.health + heal)
            .coerceAtMost(player.getAttribute(Attribute.MAX_HEALTH)!!.value)
        return true
    }
}
