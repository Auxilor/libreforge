package com.willfp.libreforge.integrations.factionsbridge.impl.conditions

import cc.javajobs.factionsbridge.FactionsBridge
import cc.javajobs.factionsbridge.bridge.infrastructure.struct.Relationship
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.get
import org.bukkit.entity.Player

object ConditionFactionRelationIs : Condition<NoCompileData>("faction_relation_is") {
    override val arguments = arguments {
        require("faction", "You must specify the faction to check relation to!")
        require("relation", "You must specify the relation!")
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
        val faction = fplayer.getFaction() ?: return false
        val target = api.getFactionByName(config.getString("faction")) ?: return false
        val required = Relationship.getRelationship(config.getString("relation").uppercase())
        return faction.getRelationshipTo(target) == required
    }
}
