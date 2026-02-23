package com.willfp.libreforge.integrations.edprisoncore.impl

import com.edwardbelt.edprison.events.EdPrisonAddMultiplierCurrency
import com.edwardbelt.edprison.utils.EconomyUtils
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.effects.templates.MultiMultiplierEffect
import com.willfp.libreforge.toDispatcher
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler

object EffectEdPrisonEconomyMultiplier : MultiMultiplierEffect<String>("edprison_economy_multiplier") {
    override val key = "economies"

    override fun getElement(key: String): String? {
        val allEconomies = EconomyUtils.getAllEconomies()
        return if (allEconomies.contains(key)) key else null
    }

    override fun getAllElements(): Collection<String> {
        return EconomyUtils.getAllEconomies()
    }

    @EventHandler(ignoreCancelled = true)
    fun handle(event: EdPrisonAddMultiplierCurrency) {
        val player = Bukkit.getPlayer(event.uuid) ?: return
        val economy = event.currency ?: return

        event.multiplier = getMultiplier(player.toDispatcher(), economy)
    }

    override fun makeCompileData(config: Config, context: ViolationContext): NoCompileData {
        val currencies = config.getStrings("economies")
        val validCurrencies = EconomyUtils.getAllEconomies()

        val invalidCurrencies = currencies.filter { it !in validCurrencies }
        if (invalidCurrencies.isNotEmpty()) {
            context.log(
                this,
                ConfigViolation(
                    "economies",
                    "Invalid economy types specified: ${invalidCurrencies.joinToString(", ")}. Valid currencies are: ${validCurrencies.joinToString(", ")}"
                )
            )
            return NoCompileData
        }
        return NoCompileData
    }
}