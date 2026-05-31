package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.items.Items
import com.willfp.eco.core.items.matches
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.event.EditableDropEvent

object EffectCancelDrops : Effect<NoCompileData>("cancel_drops") {
    override val description = "Cancels item drops from the triggering event. If on_drops is specified, only those items are removed."
    override val categories = setOf("inventory", "world")
    override val additionalInfo = listOf("Requires a trigger that provides EVENT with drop data (e.g. mine_block, entity_death).")

    override val parameters = setOf(
        TriggerParameter.EVENT
    )

    override val arguments = arguments {
        optional(
            "on_drops",
            description = "If specified, only drops matching these item types are cancelled. If omitted, all drops are cancelled.",
            type = ArgType.ITEM_LIST
        )
    }

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
