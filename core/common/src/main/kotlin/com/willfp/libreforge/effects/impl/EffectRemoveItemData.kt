package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.itemData
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter

object EffectRemoveItemData : Effect<NoCompileData>("remove_item_data") {
    override val parameters = setOf(
        TriggerParameter.ITEM
    )

    override val arguments = arguments {
        require("key", "You must specify the data key!")
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val item = data.foundItem ?: return false
        item.itemData.remove(config.getString("key"))

        return true
    }
}
