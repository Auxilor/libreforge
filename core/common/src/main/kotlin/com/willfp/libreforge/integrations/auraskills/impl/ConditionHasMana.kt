package com.willfp.libreforge.integrations.auraskills.impl

import dev.aurelium.auraskills.api.event.mana.ManaRegenerateEvent
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.*
import com.willfp.libreforge.conditions.Condition
import dev.aurelium.auraskills.api.AuraSkillsApi
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority

object ConditionHasMana : Condition<NoCompileData>("has_mana") {
    override val arguments = arguments {
        require("amount", "You must specify the amount of mana!")
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
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
