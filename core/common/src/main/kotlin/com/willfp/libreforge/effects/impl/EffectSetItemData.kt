package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getFormattedString
import com.willfp.libreforge.itemData
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter

object EffectSetItemData : Effect<NoCompileData>("set_item_data") {
    override val description = "Sets a custom data key on the trigger item to a given value."
    override val categories = setOf("inventory")

    override val parameters = setOf(
        TriggerParameter.ITEM
    )

    override val arguments = arguments {
        require(
            "key",
            "You must specify the data key!",
            description = "The custom data key to set on the item.",
            type = ArgType.STRING
        )
        require(
            "value",
            "You must specify the data value!",
            description = "The value to store under the given key.",
            type = ArgType.STRING
        )
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val item = data.foundItem ?: return false
        item.itemData[config.getString("key")] = config.getFormattedString("value", data)

        return true
    }
}
