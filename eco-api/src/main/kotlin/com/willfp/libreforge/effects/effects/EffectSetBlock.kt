package com.willfp.libreforge.effects.effects

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.items.Items
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.Triggers

class EffectSetBlock : Effect(
    "set_block",
    triggers = Triggers.withParameters(
        TriggerParameter.BLOCK,
    )
) {
    override fun handle(data: TriggerData, config: Config) {
        val block = data.block ?: data.location?.block ?: return

        val type = Items.lookup(config.getString("block")).item.type

        block.type = type
    }

    override fun validateConfig(config: Config): List<ConfigViolation> {
        val violations = mutableListOf<ConfigViolation>()

        if (!config.has("block")) violations.add(
            ConfigViolation(
                "block",
                "You must specify the block to set to!"
            )
        )

        return violations
    }
}
