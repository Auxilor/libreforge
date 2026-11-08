package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getFormattedString
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.Material

object EffectSetItemMaterial : Effect<NoCompileData>("set_item_material") {
    override val description = "Changes the material of the triggering item, keeping its enchantments, name, lore and data."
    override val categories = setOf("inventory")

    override val parameters = setOf(
        TriggerParameter.ITEM
    )

    override val arguments = arguments {
        require(
            name = "material",
            message = "You must specify the material!",
            description = "The material to change the item to. Must be an item, not air or a block-only material.",
            type = ArgType.STRING,
            example = "stone_pickaxe"
        )
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val item = data.foundItem
        val material = Material.matchMaterial(config.getFormattedString("material", data).lowercase())
            ?.takeIf { it.isItem && !it.isAir }

        if (item == null || material == null || item.type.isAir) {
            return false
        }

        item.type = material

        return true
    }
}
