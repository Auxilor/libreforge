package com.willfp.libreforge.integrations.auraskills.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.get
import dev.aurelium.auraskills.api.AuraSkillsApi
import org.bukkit.entity.Player

object ConditionHasMaxMana : Condition<NoCompileData>("has_max_mana") {
    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ): Boolean {
        val player = dispatcher.get<Player>() ?: return false
        val user = AuraSkillsApi.get().getUser(player.uniqueId)

        return user.mana == user.maxMana
    }
}
