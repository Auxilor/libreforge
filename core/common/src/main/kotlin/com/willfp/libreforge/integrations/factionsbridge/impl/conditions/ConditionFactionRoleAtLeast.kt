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

private val RANK = mapOf(
    Role.RECRUIT to 0,
    Role.NORMAL to 1,
    Role.OFFICER to 2,
    Role.CO_LEADER to 3,
    Role.LEADER to 4
)

object ConditionFactionRoleAtLeast : Condition<NoCompileData>("faction_role_at_least") {
    override val arguments = arguments {
        require("role", "You must specify the minimum role!")
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
        val requiredRank = RANK[required] ?: return false
        val playerRank = RANK[fplayer.getRole()] ?: return false
        return playerRank >= requiredRank
    }
}
