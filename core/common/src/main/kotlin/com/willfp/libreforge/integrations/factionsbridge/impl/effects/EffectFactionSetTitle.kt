package com.willfp.libreforge.integrations.factionsbridge.impl.effects

import cc.javajobs.factionsbridge.FactionsBridge
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter

object EffectFactionSetTitle : Effect<NoCompileData>("faction_set_title") {
    override val parameters = setOf(TriggerParameter.PLAYER)

    override val arguments = arguments {
        require("title", "You must specify the title to set!")
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val player = data.player ?: return false
        val api = FactionsBridge.getFactionsAPI() ?: return false
        val fplayer = api.getFPlayer(player) ?: return false
        fplayer.setTitle(config.getString("title"))
        return true
    }
}
