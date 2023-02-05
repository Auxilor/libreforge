package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.Triggers
import com.willfp.libreforge.triggers.triggers.TriggerCustom

object EffectTriggerCustom : Effect<NoCompileData>(
    "trigger_custom"
) {
    override val isPermanent = false

    override val arguments = arguments {
        require("trigger", "You must specify the custom trigger ID!")
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val player = data.player ?: return false
        val trigger = TriggerCustom.getWithID(config.getString("trigger"))
        val value = config.getDoubleFromExpression("value", data)

        trigger.invoke(player, data, value)

        return true
    }
}
