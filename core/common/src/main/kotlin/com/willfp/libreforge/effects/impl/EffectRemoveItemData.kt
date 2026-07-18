package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.itemData
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter

object EffectRemoveItemData : Effect<NoCompileData>("remove_item_data") {
    override val description = "Removes a custom persistent data entry from the triggering item by its key."
    override val categories = setOf("inventory")

    override val parameters = setOf(
        TriggerParameter.ITEM
    )

    override val arguments = arguments {
        require(
            "key",
            "You must specify the data key!",
            description = "The key of the custom item data entry to remove.",
            type = ArgType.STRING,
            example = "custom_id"
        )
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val item = data.foundItem ?: return false
        item.itemData.remove(config.getString("key"))

        return true
    }
}
