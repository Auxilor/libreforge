package com.willfp.libreforge.integrations.mcmmo.impl

import com.gmail.nossr50.api.ExperienceAPI
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter

object EffectGiveMcMMOXp : Effect<NoCompileData>("give_mcmmo_xp") {
    override val description = "Gives the player raw XP in a specified McMMO skill."
    override val categories = setOf("economy")

    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        require("amount", "You must specify the amount of xp to give!", description = "The amount of raw XP to give. Supports expressions.", type = ArgType.EXPRESSION)
        require("skill", "You must specify the skill to give xp for!", description = "The McMMO skill name to give XP for, e.g. mining.", type = ArgType.STRING)
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val player = data.player ?: return false

        ExperienceAPI.addRawXP(
            player,
            config.getString("skill"),
            config.getDoubleFromExpression("amount", player).toFloat(),
            "UNKNOWN"
        )

        return true
    }
}
