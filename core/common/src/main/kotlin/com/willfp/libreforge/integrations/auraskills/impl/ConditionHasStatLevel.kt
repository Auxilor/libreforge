package com.willfp.libreforge.integrations.auraskills.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.enumValueOfOrNull
import com.willfp.libreforge.get
import dev.aurelium.auraskills.api.AuraSkillsApi
import dev.aurelium.auraskills.api.stat.Stats
import org.bukkit.entity.Player

object ConditionHasStatLevel : Condition<NoCompileData>("has_stat_level") {
    override val arguments = arguments {
        require("stat", "You must specify the stat!")
        require("level", "You must specify the level!")
    }

    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ): Boolean {
        val player = dispatcher.get<Player>() ?: return false
        val type = enumValueOfOrNull<Stats>(config.getString("stat").uppercase()) ?: return false
        val api = AuraSkillsApi.get().getUser(player.uniqueId)

        return api.getStatLevel(type) >= config.getIntFromExpression("level", player)
    }
}