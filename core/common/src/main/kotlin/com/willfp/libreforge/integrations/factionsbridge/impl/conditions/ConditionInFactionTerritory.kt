package com.willfp.libreforge.integrations.factionsbridge.impl.conditions

import cc.javajobs.factionsbridge.FactionsBridge
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.get
import org.bukkit.entity.Player

object ConditionInFactionTerritory : Condition<NoCompileData>("in_faction_territory") {
    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ): Boolean {
        val player = dispatcher.get<Player>() ?: return false
        val api = FactionsBridge.getFactionsAPI() ?: return false
        val fplayer = api.getFPlayer(player) ?: return false
        val faction = fplayer.getFaction() ?: return false
        val claim = api.getClaim(player.location.chunk) ?: return false
        return claim.isClaimed() && claim.getFaction()?.getId() == faction.getId()
    }
}
