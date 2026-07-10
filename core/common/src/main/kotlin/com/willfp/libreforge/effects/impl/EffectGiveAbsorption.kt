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

object EffectGiveAbsorption : Effect<NoCompileData>("give_absorption") {
    override val description = "Grants the player additional absorption hearts, expanding the max absorption cap if needed."
    override val categories = setOf("player", "attribute")

    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        require(
            "amount",
            "You must specify the amount of absorption hearts!",
            description = "The number of absorption hearts to add. Supports expressions.",
            type = ArgType.EXPRESSION,
            example = "%level%"
        )
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val player = data.player ?: return false
        val newAmount = player.absorptionAmount + config.getDoubleFromExpression("amount", data)
        player.getAttribute(Attribute.MAX_ABSORPTION)?.let { attr ->
            if (newAmount > attr.value) {
                attr.baseValue = newAmount
            }
        }
        player.absorptionAmount = newAmount
        return true
    }
}
