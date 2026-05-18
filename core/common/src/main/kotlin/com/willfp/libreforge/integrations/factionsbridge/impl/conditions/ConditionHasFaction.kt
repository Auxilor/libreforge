package com.willfp.libreforge.integrations.factionsbridge.impl.conditions

import cc.javajobs.factionsbridge.FactionsBridge
import cc.javajobs.factionsbridge.bridge.events.FactionJoinEvent
import cc.javajobs.factionsbridge.bridge.events.FactionLeaveEvent
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.get
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.updateEffects
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler

object ConditionHasFaction : Condition<NoCompileData>("has_faction") {
    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ): Boolean {
        val player = dispatcher.get<Player>() ?: return false
        val api = FactionsBridge.getFactionsAPI() ?: return false
        val fplayer = api.getFPlayer(player) ?: return false
        return fplayer.hasFaction()
    }

    @EventHandler(ignoreCancelled = true)
    fun onJoin(event: FactionJoinEvent) {
        event.getFPlayer().getPlayer()?.toDispatcher()?.updateEffects()
    }

    @EventHandler(ignoreCancelled = true)
    fun onLeave(event: FactionLeaveEvent) {
        event.getFPlayer().getPlayer()?.toDispatcher()?.updateEffects()
    }
}
