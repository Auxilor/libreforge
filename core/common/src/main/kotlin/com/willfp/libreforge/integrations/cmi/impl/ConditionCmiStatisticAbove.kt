package com.willfp.libreforge.integrations.cmi.impl

import com.Zrips.CMI.CMI
import com.Zrips.CMI.Modules.Statistics.StatsManager.CMIStatistic
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.get
import org.bukkit.entity.Player

object ConditionCmiStatisticAbove : Condition<NoCompileData>("cmi_statistic_above") {
    override val description = "Passes when a CMI statistic of the player is above a specified amount."
    override val categories = setOf("player")
    override val additionalInfo = listOf(
        "Requires the CMI plugin."
    )

    override val arguments = arguments {
        require(
            "statistic",
            "You must specify a valid CMI statistic!",
            Config::getString
        ) { name ->
            lookup(name) != null
        }
        describe(
            "statistic",
            description = "The CMI statistic to read.",
            type = ArgType.STRING,
            enumClass = CMIStatistic::class
        )
        require(
            "amount",
            "You must specify the amount!",
            description = "The value the statistic must exceed. Supports expressions.",
            type = ArgType.EXPRESSION
        )
    }

    private fun lookup(name: String?): CMIStatistic? =
        CMIStatistic.values().firstOrNull { it.name.equals(name, ignoreCase = true) }

    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ): Boolean {
        val player = dispatcher.get<Player>() ?: return false
        val user = CMI.getInstance()?.playerManager?.getUser(player) ?: return false
        val statistic = lookup(config.getString("statistic")) ?: return false

        return user.getStatistic(statistic) > config.getDoubleFromExpression("amount", player)
    }
}
