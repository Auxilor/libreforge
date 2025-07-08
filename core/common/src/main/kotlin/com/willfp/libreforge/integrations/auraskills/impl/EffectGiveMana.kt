package com.willfp.libreforge.integrations.auraskills.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import dev.aurelium.auraskills.api.AuraSkillsApi

object EffectGiveMana : Effect<NoCompileData>("give_mana") {
    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        require("amount", "You must specify the amount to add / subtract!")
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val player = data.player ?: return false
        val auraSkills = AuraSkillsApi.get()
        val user = auraSkills.getUser(player.uniqueId)

        user.mana += config.getDoubleFromExpression("amount", data)

        return true
    }
}