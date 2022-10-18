package com.willfp.libreforge.effects.effects

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.items.Items
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.Triggers

class EffectDropItem : Effect(
    "drop_item",
    triggers = Triggers.withParameters(
        TriggerParameter.LOCATION
    )
) {
    override fun handle(data: TriggerData, config: Config) {
        val location = data.location ?: return

        val item = Items.lookup(config.getString("item")).item

        location.world?.dropItemNaturally(location, item)
    }

    override fun validateConfig(config: Config): List<ConfigViolation> {
        val violations = mutableListOf<ConfigViolation>()

        if (!config.has("item")) violations.add(
            ConfigViolation(
                "item",
                "You must specify the item to gidropve!"
            )
        )

        return violations
    }
}
