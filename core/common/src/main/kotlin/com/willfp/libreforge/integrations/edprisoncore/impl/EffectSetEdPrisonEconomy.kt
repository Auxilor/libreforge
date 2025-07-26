package com.willfp.libreforge.integrations.edprisoncore.impl

import com.edwardbelt.edprison.utils.EconomyUtils
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter

object EffectSetEdPrisonEconomy  : Effect<NoCompileData>("set_edprison_economy") {
    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        require("type", "You must specify the type of economy to give!")
        require("amount", "You must specify the amount of economy to give!")
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val player = data.player ?: return false
        val uuid = player.uniqueId

        val currency = config.getString("type") ?: return false
        val amount = config.getDoubleFromExpression("amount", data)

        EconomyUtils.setEco(uuid, currency, amount)

        return true
    }
}