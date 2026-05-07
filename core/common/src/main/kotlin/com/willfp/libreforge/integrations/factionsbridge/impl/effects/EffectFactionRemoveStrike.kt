package com.willfp.libreforge.integrations.factionsbridge.impl.effects

import cc.javajobs.factionsbridge.FactionsBridge
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.entity.Player

object EffectFactionRemoveStrike : Effect<NoCompileData>("faction_remove_strike") {
    override val parameters = setOf(TriggerParameter.PLAYER)

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val player = data.player ?: return false
        val api = FactionsBridge.getFactionsAPI() ?: return false
        val faction = api.getFaction(player as Player) ?: return false
        if (faction.isServerFaction()) return false
        val reason = if (config.has("reason")) config.getString("reason") else "libreforge"
        faction.removeStrike("libreforge", reason)
        return true
    }
}
