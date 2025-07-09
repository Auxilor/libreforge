package com.willfp.libreforge.integrations.auraskills.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.effects.Identifiers
import com.willfp.libreforge.get
import dev.aurelium.auraskills.api.AuraSkillsApi
import org.bukkit.entity.Player

object EffectMultiplyMana : Effect<NoCompileData>("multiply_mana") {
    override val arguments = arguments {
        require("multiplier", "You must specify the multiplier!")
    }

    override fun onEnable(
        dispatcher: Dispatcher<*>,
        config: Config,
        identifiers: Identifiers,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ) {
        val player = dispatcher.get<Player>() ?: return
        val auraSkills = AuraSkillsApi.get()
        val user = auraSkills.getUser(player.uniqueId)
        val multiplier = config.getDoubleFromExpression("multiplier", player)

        user.mana *= multiplier
    }
}