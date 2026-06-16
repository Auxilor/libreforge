package com.willfp.libreforge.integrations.factionsbridge.impl.conditions

import cc.javajobs.factionsbridge.FactionsBridge
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.get
import org.bukkit.entity.Player

object ConditionFactionPowerAbove : Condition<NoCompileData>("faction_power_above") {
    override val arguments = arguments {
        require("power", "You must specify the power threshold!")
    }

    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ): Boolean {
        val player = dispatcher.get<Player>() ?: return false
        val api = FactionsBridge.getFactionsAPI() ?: return false
        val faction = api.getFaction(player) ?: return false
        if (faction.isServerFaction()) return false
        return faction.getPower() > config.getDouble("power")
    }
}
