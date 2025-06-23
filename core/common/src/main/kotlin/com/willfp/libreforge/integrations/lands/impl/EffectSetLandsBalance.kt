package com.willfp.libreforge.integrations.lands.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.getOrElse
import com.willfp.libreforge.plugin
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import me.angeschossen.lands.api.LandsIntegration

object EffectSetLandsBalance : Effect<NoCompileData>("set_lands_balance") {
    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        require("amount", "You must specify the amount of money to give!")
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val player = data.player ?: return false
        val location = data.location ?: return false

        val landsAPI = LandsIntegration.of(plugin)
        val area = landsAPI.getArea(location) ?: return false

        val onlyTrusted = config.getOrElse("only_trusted", true) { it.toBooleanStrictOrNull() ?: true }
        if (onlyTrusted && !area.isTrusted(player.uniqueId)) {
            return false
        }

        val amount = config.getDoubleFromExpression("amount", data)
        area.land.balance = amount

        return true
    }
}
