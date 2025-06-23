package com.willfp.libreforge.integrations.lands.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.plugin
import com.willfp.libreforge.triggers.TriggerData
import me.angeschossen.lands.api.LandsIntegration
import me.angeschossen.lands.api.war.War
import org.bukkit.entity.Player

object FilterAtWarWithVictim : Filter<NoCompileData, Boolean>("at_war_with_victim") {
    override fun getValue(config: Config, data: TriggerData?, key: String): Boolean {
        return config.getBool(key)
    }

    override fun isMet(data: TriggerData, value: Boolean, compileData: NoCompileData): Boolean {
        val attacker = data.player as? Player ?: return true
        val victim = data.victim as? Player ?: return true

        val landsIntegration = LandsIntegration.of(plugin) ?: return true

        val attackerLandPlayer = landsIntegration.getLandPlayer(attacker.uniqueId) ?: return true
        val victimLandPlayer = landsIntegration.getLandPlayer(victim.uniqueId) ?: return true

        val attackerUuid = attackerLandPlayer.player.uniqueId.toString()
        val victimUuid = victimLandPlayer.player.uniqueId.toString()

        val warsRaw = attackerLandPlayer.wars

        val areAtWar = warsRaw.any { warRaw ->
            val war = warRaw as? War ?: return@any false

            val attackerSideUuids = war.onlineAttackers.map { it.landPlayer.player.uniqueId.toString() }.toSet()
            val defenderSideUuids = war.onlineDefenders.map { it.landPlayer.player.uniqueId.toString() }.toSet()

            (attackerSideUuids.contains(attackerUuid) && defenderSideUuids.contains(victimUuid)) ||
                    (attackerSideUuids.contains(victimUuid) && defenderSideUuids.contains(attackerUuid))
        }

        return value == areAtWar
    }
}
