package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.drops.DropQueue
import com.willfp.eco.core.items.Items
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.Triggers

class EffectDropItemForPlayer : Effect(
    "drop_item_for_player",
    triggers = Triggers.withParameters(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION
    )
) {
    override val arguments = arguments {
        require("item", "You must specify the item to drop!")
    }

    override fun handle(data: TriggerData, config: Config) {
        val player = data.player ?: return
        val location = data.location ?: return

        val item = Items.lookup(config.getString("item")).item

        DropQueue(player)
            .setLocation(location)
            .addItem(item)
            .push()
    }
}
