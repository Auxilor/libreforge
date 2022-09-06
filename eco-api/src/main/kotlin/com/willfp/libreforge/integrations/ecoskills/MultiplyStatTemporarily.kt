package com.willfp.libreforge.integrations.ecoskills

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.NumberUtils
import com.willfp.ecoskills.api.EcoSkillsAPI
import com.willfp.ecoskills.api.modifier.ModifierOperation
import com.willfp.ecoskills.api.modifier.PlayerStatModifier
import com.willfp.ecoskills.stats.Stats
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.getIntFromExpression
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.Triggers

class MultiplyStatTemporarily : Effect(
    "multiply_stat_temporarily",
    Triggers.withParameters(
        TriggerParameter.PLAYER
    )
) {
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

    override fun validateConfig(config: Config): List<ConfigViolation> {
        val violations = mutableListOf<ConfigViolation>()

        if (!config.has("stat")) violations.add(
            ConfigViolation(
                "stat",
                "You must specify the stat!"
            )
        )

        if (!config.has("multiplier")) violations.add(
            ConfigViolation(
                "amount",
                "You must specify the multiplier!"
            )
        )

        if (!config.has("duration")) violations.add(
            ConfigViolation(
                "duration",
                "You must specify the duration for the boost!"
            )
        )

        return violations
    }
}
