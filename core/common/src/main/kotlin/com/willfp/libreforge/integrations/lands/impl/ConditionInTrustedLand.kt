package com.willfp.libreforge.integrations.lands.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.get
import com.willfp.libreforge.plugin
import com.willfp.libreforge.triggers.TriggerData
import me.angeschossen.lands.api.LandsIntegration
import org.bukkit.entity.Player

object ConditionInTrustedLand : Condition<NoCompileData>("in_trusted_land") {
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

        return area.isTrusted(player.uniqueId)
    }
}