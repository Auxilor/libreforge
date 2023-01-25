package com.willfp.libreforge.effects.effects

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.drops.DropQueue
import com.willfp.eco.core.items.Items
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.Triggers

class EffectGiveItem : Effect(
    "give_item",
    triggers = Triggers.withParameters(
        TriggerParameter.PLAYER
    )
) {
    override val arguments = arguments {
        require("item", "You must specify the item to give!")
    }

    override fun handle(data: TriggerData, config: Config) {
        val player = data.player ?: return

        val item = Items.lookup(config.getString("item")).item

        DropQueue(player)
            .addItem(item)
            .forceTelekinesis()
            .push()
    }
}
