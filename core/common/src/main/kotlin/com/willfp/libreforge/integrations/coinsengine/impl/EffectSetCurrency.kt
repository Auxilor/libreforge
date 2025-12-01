package com.willfp.libreforge.integrations.coinsengine.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import su.nightexpress.coinsengine.api.CoinsEngineAPI

object EffectSetCurrency : Effect<String?>("set_currency") {
    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        require("currency", "You must specify the currency to set!")
        require("amount", "You must specify the amount of currency to set!")
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: String?): Boolean {
        val player = data.player ?: return false
        val uuid = player.uniqueId

        val currency = config.getString("currency")
        val amount = config.getDoubleFromExpression("amount", data)

        CoinsEngineAPI.getCurrency(currency)?.let {
            return CoinsEngineAPI.setBalance(uuid, it, amount)
        }

        return false
    }

    override fun makeCompileData(config: Config, context: ViolationContext): String? {
        val currency = config.getString("currency")
        val validCurrencies = CoinsEngineAPI.getCurrencies().map { it.id }

        if (currency !in validCurrencies) {
            context.log(
                this,
                ConfigViolation(
                    "currency",
                    "You must specify the currency to set! Valid currencies are: ${validCurrencies.joinToString(", ")}"
                )
            )
            return null
        }
        return currency
    }
}