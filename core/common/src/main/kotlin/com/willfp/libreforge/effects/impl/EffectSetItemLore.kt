package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.toComponent
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getFormattedStrings
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter

object EffectSetItemLore : Effect<NoCompileData>("set_item_lore") {
    override val description = "Sets the lore of the triggering item."
    override val categories = setOf("inventory")

    override val parameters = setOf(
        TriggerParameter.ITEM
    )

    override val arguments = arguments {
        require(
            "lore",
            "You must specify the lore to set!",
            description = "The lines of lore to give the item. Supports placeholders.",
            type = ArgType.STRING_LIST,
            example = listOf("&7Forged by %player_name%", "&7Level %level%")
        )
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val item = data.foundItem ?: return false
        val meta = item.itemMeta ?: return false

        meta.lore(config.getFormattedStrings("lore", data).map { it.toComponent() })
        item.itemMeta = meta

        return true
    }
}
