package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getFormattedString
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.NamespacedKey

object EffectSetItemModel : Effect<NoCompileData>("set_item_model") {
    override val description = "Sets the item model of the triggering item, keeping its material and meta."
    override val categories = setOf("inventory")

    override val parameters = setOf(
        TriggerParameter.ITEM
    )

    override val arguments = arguments {
        require(
            name = "model",
            message = "You must specify the item model!",
            description = "The item model to apply, as a namespaced ID. The namespace defaults to minecraft.",
            type = ArgType.STRING,
            example = "minecraft:stone_pickaxe"
        )
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val item = data.foundItem
        val meta = item?.itemMeta
        val model = NamespacedKey.fromString(config.getFormattedString("model", data).lowercase())

        if (item == null || meta == null || model == null) {
            return false
        }

        meta.itemModel = model
        item.itemMeta = meta

        return true
    }
}
