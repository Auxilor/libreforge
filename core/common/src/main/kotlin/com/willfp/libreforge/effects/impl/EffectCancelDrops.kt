package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.items.Items
import com.willfp.eco.core.items.matches
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.event.EditableDropEvent

object EffectCancelDrops : Effect<NoCompileData>("cancel_drops") {
    override val parameters = setOf(
        TriggerParameter.EVENT
    )

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val event = data.event as? EditableDropEvent ?: return false

        val onDrops = config.getStrings("on_drops").map { Items.lookup(it) }

        if (onDrops.isEmpty()) {
            event.drops.clear()
        } else {
            event.drops.removeIf { stack -> onDrops.any { it.matches(stack) } }
        }

        return true
    }
}
