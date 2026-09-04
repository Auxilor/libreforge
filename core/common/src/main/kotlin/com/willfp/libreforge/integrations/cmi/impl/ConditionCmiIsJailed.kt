package com.willfp.libreforge.integrations.cmi.impl

import com.Zrips.CMI.CMI
import com.Zrips.CMI.events.CMIPlayerJailEvent
import com.Zrips.CMI.events.CMIPlayerUnjailEvent
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.get
import org.bukkit.entity.Player
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.updateEffects
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority

object ConditionCmiIsJailed : Condition<NoCompileData>("cmi_is_jailed") {
    override val description = "Passes when the player is in jail."
    override val categories = setOf("player")
    override val additionalInfo = listOf(
        "Requires the CMI plugin."
    )

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun handleJail(event: CMIPlayerJailEvent) {
        event.user?.player?.toDispatcher()?.updateEffects()
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun handleUnjail(event: CMIPlayerUnjailEvent) {
        event.user?.player?.toDispatcher()?.updateEffects()
    }

    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ): Boolean {
        val player = dispatcher.get<Player>() ?: return false
        val user = CMI.getInstance()?.playerManager?.getUser(player) ?: return false

        return user.isJailed
    }
}
