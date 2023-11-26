package com.willfp.libreforge.integrations.tab.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.get
import me.neznamy.tab.api.TabAPI
import org.bukkit.entity.Player

object ConditionHasScoreboardVisible: Condition<NoCompileData>("has_scoreboard_visible") {
    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ): Boolean {
        val bukkit = dispatcher.get<Player>() ?: return false
        val player = TabAPI.getInstance().getPlayer(bukkit.uniqueId) ?: return false

        return TabAPI.getInstance().scoreboardManager?.hasScoreboardVisible(player) == true
    }
}
