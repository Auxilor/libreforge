package com.willfp.libreforge.integrations.lands.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.get
import com.willfp.libreforge.plugin
import me.angeschossen.lands.api.LandsIntegration
import org.bukkit.entity.Player

object ConditionInEnemyClaim : Condition<NoCompileData>("in_enemy_claim") {
    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ): Boolean {
        val player = dispatcher.get<Player>() ?: return false
        val location = dispatcher.location ?: return false

        val landsAPI = LandsIntegration.of(plugin)
        val area = landsAPI.getArea(location) ?: return false
        val currentLand = area.land

        val playerLands = landsAPI.getLandPlayer(player.uniqueId)?.lands ?: return false

        return playerLands.any { currentLand.isEnemy(it) }
    }
}
