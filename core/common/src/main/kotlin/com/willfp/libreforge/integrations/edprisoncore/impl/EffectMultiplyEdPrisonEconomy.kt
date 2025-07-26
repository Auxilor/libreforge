package com.willfp.libreforge.integrations.edprisoncore.impl

import com.edwardbelt.edprison.events.EdPrisonAddMultiplierCurrency
import com.edwardbelt.edprison.utils.EconomyUtils
import com.willfp.libreforge.effects.templates.MultiMultiplierEffect
import com.willfp.libreforge.toDispatcher
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler

object EffectMultiplyEdPrisonEconomy : MultiMultiplierEffect<String>("multiply_edprison_economy") {
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

        event.amount *= getMultiplier(player.toDispatcher(), economy)
    }
}