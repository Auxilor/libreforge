package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.toComponent
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getFormattedString
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter

object EffectRenameItem : Effect<NoCompileData>("rename_item") {
    override val description = "Sets the display name of the triggering item."
    override val categories = setOf("inventory")

    override val parameters = setOf(
        TriggerParameter.ITEM
    )

    override val arguments = arguments {
        require(
            "name",
            "You must specify the name to set!",
            description = "The display name to give the item. Supports placeholders.",
            type = ArgType.STRING,
            example = "&6%player_name%'s Sword"
        )
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val item = data.foundItem ?: return false
        val meta = item.itemMeta ?: return false

        meta.displayName(config.getFormattedString("name", data).toComponent())
        item.itemMeta = meta

        return true
    }
}
