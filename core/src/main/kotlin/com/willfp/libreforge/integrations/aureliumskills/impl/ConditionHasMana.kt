package com.willfp.libreforge.integrations.aureliumskills.impl

import com.archyx.aureliumskills.api.AureliumAPI
import com.archyx.aureliumskills.api.event.ManaRegenerateEvent
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.triggers.Dispatcher
import com.willfp.libreforge.triggers.get
import com.willfp.libreforge.updateEffects
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority

object ConditionHasMana : Condition<NoCompileData>("has_mana") {
    override val arguments = arguments {
        require("amount", "You must specify the amount of mana!")
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun handle(event: ManaRegenerateEvent) {
        val player = event.player

        player.updateEffects()
    }

    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ): Boolean {
        val player = dispatcher.get<Player>() ?: return false

        return AureliumAPI.getMana(player) > config.getDoubleFromExpression("amount", player)
    }
}
