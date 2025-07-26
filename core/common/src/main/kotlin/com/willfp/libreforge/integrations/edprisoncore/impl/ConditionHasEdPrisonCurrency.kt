package com.willfp.libreforge.integrations.edprisoncore.impl

import com.edwardbelt.edprison.utils.EconomyUtils
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.get
import org.bukkit.entity.Player

object ConditionHasEdPrisonCurrency : Condition<NoCompileData>("has_edprison_economy") {
    override val arguments = arguments {
        require("type", "You must specify the economy type!")
        require("amount", "You must specify the level!")
    }

    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ): Boolean {
        val player = dispatcher.get<Player>()?.uniqueId ?: return false
        val amount = config.getInt("amount")
        val currency = config.getString("type")

        return EconomyUtils.getEco(player, currency) >= amount
    }
}