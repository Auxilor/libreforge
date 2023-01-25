package com.willfp.libreforge.integrations.ecoskills

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.NumberUtils
import com.willfp.ecoskills.api.EcoSkillsAPI
import com.willfp.ecoskills.api.modifier.ModifierOperation
import com.willfp.ecoskills.api.modifier.PlayerStatModifier
import com.willfp.ecoskills.stats.Stats
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.getIntFromExpression
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.Triggers

class EffectMultiplyStatTemporarily : Effect(
    "multiply_stat_temporarily",
    Triggers.withParameters(
        TriggerParameter.PLAYER
    )
) {
    override val arguments = arguments {
        require("stat", "You must specify the stat!")
        require("multiplier", "You must specify the multiplier!")
        require("duration", "You must specify the duration for the boost!")
    }

    private val api = EcoSkillsAPI.getInstance()

    override fun handle(data: TriggerData, config: Config) {
        val player = data.player ?: return
        val stat = Stats.getByID(config.getString("stat")) ?: return
        val amount = config.getDoubleFromExpression("multiplier", data)
        val key = plugin.namespacedKeyFactory.create("mst_${NumberUtils.randInt(0, 1000000)}")

        api.addStatModifier(
            player,
            PlayerStatModifier(key, stat, amount, ModifierOperation.MULTIPLY)
        )

        plugin.scheduler.runLater(config.getIntFromExpression("duration", data).toLong()) {
            api.removeStatModifier(player, key)
        }
    }
}
