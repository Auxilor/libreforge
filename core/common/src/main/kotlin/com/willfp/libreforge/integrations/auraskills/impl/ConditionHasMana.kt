package com.willfp.libreforge.integrations.auraskills.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.get
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.updateEffects
import dev.aurelium.auraskills.api.AuraSkillsApi
import dev.aurelium.auraskills.api.event.mana.ManaRegenerateEvent
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority

object ConditionHasMana : Condition<NoCompileData>("has_mana") {
    override val arguments = arguments {
        require("amount", "You must specify the amount of mana!")
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun handle(event: ManaRegenerateEvent) {
        event.player.toDispatcher().updateEffects()
    }

    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ): Boolean {
        val player = dispatcher.get<Player>() ?: return false

        return AuraSkillsApi.get().getUser(player.uniqueId).mana > config.getDoubleFromExpression("amount", player)
    }
}
