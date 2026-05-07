package com.willfp.libreforge.integrations.factionsbridge.impl.conditions

import cc.javajobs.factionsbridge.FactionsBridge
import cc.javajobs.factionsbridge.bridge.infrastructure.struct.Role
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.get
import org.bukkit.entity.Player

object ConditionIsFactionRole : Condition<NoCompileData>("is_faction_role") {
    override val arguments = arguments {
        require("role", "You must specify the role!")
    }

    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ): Boolean {
        val player = dispatcher.get<Player>() ?: return false
        val api = FactionsBridge.getFactionsAPI() ?: return false
        val fplayer = api.getFPlayer(player) ?: return false
        val required = Role.getRole(config.getString("role").uppercase()) ?: return false
        return fplayer.getRole() == required
    }
}
