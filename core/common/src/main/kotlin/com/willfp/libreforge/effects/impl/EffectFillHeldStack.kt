package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getIntFromExpression
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter

object EffectFillHeldStack : Effect<NoCompileData>("fill_held_stack") {
    override val description = "Fills the stack in the player's main hand up to its maximum size."
    override val categories = setOf("player", "inventory")

    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        optional(
            "amount",
            description = "The stack size to set. Defaults to the item's maximum stack size. Supports expressions.",
            type = ArgType.EXPRESSION,
            example = "64"
        )
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val player = data.player ?: return false

        val item = player.inventory.itemInMainHand

        if (item.type.isAir) {
            return false
        }

        val amount = if (config.has("amount")) {
            config.getIntFromExpression("amount", data)
        } else {
            item.maxStackSize
        }

        item.amount = amount.coerceIn(1, item.maxStackSize)
        player.inventory.setItemInMainHand(item)

        return true
    }
}
