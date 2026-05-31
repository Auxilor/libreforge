package com.willfp.libreforge.integrations.edprisoncore.impl

import com.edwardbelt.edprison.utils.EconomyUtils
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.get
import org.bukkit.entity.Player

object ConditionHasEdPrisonCurrency : Condition<String?>("has_edprison_economy") {
    override val description = "Passes when the player has at least the specified amount of a given EDPrisonCore currency."
    override val categories = setOf("economy", "player")
    override val additionalInfo = listOf("Requires the EDPrisonCore plugin.")

    override val arguments = arguments {
        require(
            "type",
            "You must specify the economy type!",
            description = "The EDPrisonCore economy/currency type to check.",
            type = ArgType.STRING
        )
        require(
            "amount",
            "You must specify the level!",
            description = "The minimum amount of the currency the player must have.",
            type = ArgType.EXPRESSION
        )
    }

    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: String?
    ): Boolean {
        val player = dispatcher.get<Player>()?.uniqueId ?: return false
        val amount = config.getInt("amount")
        val currency = config.getString("type")

        return EconomyUtils.getEco(player, currency) >= amount
    }

    override fun makeCompileData(config: Config, context: ViolationContext): String? {
        val currency = config.getString("type")
        val validCurrencies = EconomyUtils.getAllEconomies()

        if (currency !in validCurrencies) {
            context.log(
                this,
                ConfigViolation(
                    "type",
                    "You must specify the type of economy to give! Valid currencies are: ${
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