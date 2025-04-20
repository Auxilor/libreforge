package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getIntFromExpression
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter

object EffectSetCustomModelData : Effect<NoCompileData>("set_custom_model_data") {
    override val parameters = setOf(
        TriggerParameter.ITEM
    )

    override val arguments = arguments {
        require("model", "You must specify the custom model data ID!")
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val item = data.foundItem ?: return false
        val meta = item.itemMeta ?: return false

        meta.setCustomModelData(config.getIntFromExpression("model", data))
        item.itemMeta = meta

        return true
    }
}
