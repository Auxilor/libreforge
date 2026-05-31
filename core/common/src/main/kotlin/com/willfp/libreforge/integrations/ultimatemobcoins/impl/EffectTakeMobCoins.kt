package com.willfp.libreforge.integrations.ultimatemobcoins.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import nl.chimpgamer.ultimatemobcoins.paper.UltimateMobCoinsPlugin
import java.math.MathContext

class EffectTakeMobCoins(private val plugin: UltimateMobCoinsPlugin) : Effect<NoCompileData>("take_mob_coins") {
    override val description = "Removes a specified amount of UltimateMobCoins from the player."
    override val categories = setOf("economy")

    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        require("amount", "You must specify the amount of mobcoins to take!", description = "The amount of mob coins to remove from the player. Supports expressions.", type = ArgType.EXPRESSION)
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val player = data.player ?: return false
        val user = plugin.userManager.getIfLoaded(player) ?: return false
        val amount = config.getDoubleFromExpression("amount", data)
        plugin.userManager.withdrawCoinsAsync(user, amount.toBigDecimal(MathContext(3)))
        return true
    }
}