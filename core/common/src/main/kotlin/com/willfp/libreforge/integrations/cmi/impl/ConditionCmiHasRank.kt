package com.willfp.libreforge.integrations.cmi.impl

import com.Zrips.CMI.CMI
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.containsIgnoreCase
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.get
import org.bukkit.entity.Player

object ConditionCmiHasRank : Condition<NoCompileData>("cmi_has_rank") {
    override val description = "Passes when the player has one of the specified CMI ranks."
    override val categories = setOf("player")
    override val additionalInfo = listOf(
        "Requires the CMI plugin."
    )

    override val arguments = arguments {
        require(
            "rank",
            "You must specify the rank names!",
            description = "The CMI rank name(s) to check against. Matched case-insensitively.",
            type = ArgType.STRING_LIST
        )
    }

    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ): Boolean {
        val player = dispatcher.get<Player>() ?: return false
        val user = CMI.getInstance()?.playerManager?.getUser(player) ?: return false
        val rank = user.rank?.name ?: return false

        return config.getStrings("rank").containsIgnoreCase(rank)
    }
}
