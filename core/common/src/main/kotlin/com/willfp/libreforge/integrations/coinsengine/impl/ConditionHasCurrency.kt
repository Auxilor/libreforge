package com.willfp.libreforge.integrations.coinsengine.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.*
import com.willfp.libreforge.conditions.Condition
import org.bukkit.entity.Player
import su.nightexpress.coinsengine.api.CoinsEngineAPI

object ConditionHasCurrency : Condition<String?>("has_currency") {
    override val arguments = arguments {
        require("currency", "You must specify the currency!")
        require("amount", "You must specify the amount of currency!")
    }

    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: String?
    ): Boolean {
        val player = dispatcher.get<Player>()?.uniqueId ?: return false
        val amount = config.getInt("amount")
        val currency = config.getString("currency")

        CoinsEngineAPI.getCurrency(currency)?.let {
            return CoinsEngineAPI.getBalance(player, it) >= amount
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
                    "You must specify the type of currency! Valid currencies are: ${
                        validCurrencies.joinToString(
                            ", "
                        )
                    }"
                )
            )
            return null
        }
        return currency
    }
}