package com.willfp.libreforge.integrations.factionsbridge.impl.effects

import cc.javajobs.factionsbridge.FactionsBridge
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.entity.Player

object EffectFactionSetBank : Effect<NoCompileData>("faction_set_bank") {
    override val parameters = setOf(TriggerParameter.PLAYER)

    override val arguments = arguments {
        require("amount", "You must specify the amount to set!")
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val player = data.player ?: return false
        val api = FactionsBridge.getFactionsAPI() ?: return false
        val faction = api.getFaction(player as Player) ?: return false
        if (faction.isServerFaction()) return false
        faction.setBank(config.getDoubleFromExpression("amount", data))
        return true
    }
}
