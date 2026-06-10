package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getIntFromExpression
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.updateHolders
import org.bukkit.Material

object EffectConsumeHeldItem : Effect<NoCompileData>("consume_held_item") {
    override val description = "Removes a number of items from the player's main hand."
    override val categories = setOf("inventory")

    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        require(
            "amount",
            "You must specify the amount of items to consume!",
            description = "The number of items to remove from the main hand. Supports expressions.",
            type = ArgType.EXPRESSION
        )
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val player = data.player ?: return false

        val amount = config.getIntFromExpression("amount", data)

        val item = player.inventory.itemInMainHand

        val newAmount = item.amount - amount
        if (newAmount <= 0) {
            @Suppress("DEPRECATION")
            item.type = Material.AIR
        } else {
            item.amount = newAmount
        }

        player.inventory.setItemInMainHand(item)

        player.toDispatcher().updateHolders()

        return true
    }
}
