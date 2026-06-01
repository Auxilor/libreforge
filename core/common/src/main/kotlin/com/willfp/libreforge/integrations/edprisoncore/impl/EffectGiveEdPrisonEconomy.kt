package com.willfp.libreforge.integrations.edprisoncore.impl

import com.edwardbelt.edprison.utils.EconomyUtils
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter

object EffectGiveEdPrisonEconomy : Effect<String?>("give_edprison_economy") {
    override val description = "Gives the player a specified amount of an EdPrison economy currency."
    override val categories = setOf("economy")

    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        require(
            "type",
            "You must specify the type of economy to give!",
            description = "The EdPrison economy currency ID to give.",
            type = ArgType.STRING
        )
        require(
            "amount",
            "You must specify the amount of economy to give!",
            description = "The amount of currency to give. Supports expressions.",
            type = ArgType.EXPRESSION
        )
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: String?): Boolean {
        val player = data.player ?: return false
        val uuid = player.uniqueId

        val currency = config.getString("type")
        val amount = config.getDoubleFromExpression("amount", data)

        EconomyUtils.addEconomy(uuid, currency, amount)

        return true
    }

    override fun makeCompileData(config: Config, context: ViolationContext): String? {
        val currency = config.getString("type")
        val validCurrencies = EconomyUtils.getAllEconomies()

        if (currency !in validCurrencies) {
            context.log(
                this,
                ConfigViolation(
                    "type",
                    "You must specify the type of economy to give! Valid currencies are: ${validCurrencies.joinToString(", ")}"
                )
            )
            return null
        }
        return currency
    }
}