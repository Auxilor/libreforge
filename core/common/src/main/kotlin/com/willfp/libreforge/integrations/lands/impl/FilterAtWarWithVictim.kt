package com.willfp.libreforge.integrations.lands.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.GlobalDispatcher.dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.get
import com.willfp.libreforge.plugin
import com.willfp.libreforge.triggers.TriggerData
import me.angeschossen.lands.api.LandsIntegration
import org.bukkit.entity.Player

object FilterAtWarWithVictim : Filter<NoCompileData, Boolean>("at_war_with_victim") {
    override fun getValue(config: Config, data: TriggerData?, key: String): Boolean {
        return config.getBool(key)
    }

    override fun isMet(data: TriggerData, value: Boolean, compileData: NoCompileData): Boolean {
        val victim = data.victim as? Player ?: return true
        val player = data.player ?: return true

        val landsAPI = LandsIntegration.of(plugin)

        // Get player land memberships
        val playerLand = landsAPI.getLandPlayer(player.uniqueId)?.lands?: return true
        val victimLand = landsAPI.getLandPlayer(victim.uniqueId)?.lands?: return true

        return value = landsAPI.getArea(player.location).

//        return value == landsAPI.war.isAtWar(playerLand, victimLand)
    }
}