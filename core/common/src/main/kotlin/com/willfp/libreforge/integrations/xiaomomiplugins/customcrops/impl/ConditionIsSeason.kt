package com.willfp.libreforge.integrations.xiaomomiplugins.customcrops.impl

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
        require(listOf("season", "seasons"), "You must specify the season(s)!")
    }

    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ): Boolean {
        val player = dispatcher.get<Player>() ?: return false
        val season = BukkitCustomCropsPlugin.getInstance().worldManager.getSeason(player.world)

        val seasons = mutableListOf<String>()
        config.getString("season")?.let { seasons.add(it) }
        seasons.addAll(config.getStrings("seasons"))

        return seasons
            .map { it.uppercase() }
            .contains(season.name.uppercase())
    }
}