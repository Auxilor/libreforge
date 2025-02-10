package com.willfp.libreforge.integrations.customcrops.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.get
import net.momirealms.customcrops.api.BukkitCustomCropsPlugin
import org.bukkit.entity.Player

object ConditionIsSeason : Condition<NoCompileData>("is_season") {
    override val arguments = arguments {
        require("season", "You must specify the season!")
    }

    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ): Boolean {
        val player = dispatcher.get<Player>() ?: return false
        val season = BukkitCustomCropsPlugin.getInstance().worldManager.getSeason(player.world)

        return config.getStrings("seasons")
            .map { it.uppercase() }
            .contains(season.name.uppercase())
    }
}