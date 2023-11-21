package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.NamespacedKey
import org.bukkit.Registry
import org.bukkit.inventory.meta.ArmorMeta
import org.bukkit.inventory.meta.trim.ArmorTrim

object EffectSetArmorTrim : Effect<NoCompileData>("set_armor_trim") {
    override val parameters = setOf(
        TriggerParameter.ITEM
    )

    override val arguments = arguments {
        require("pattern", "You must specify a valid trim pattern!", Config::getString) {
            Registry.TRIM_PATTERN.get(NamespacedKey.minecraft(it)) != null
        }
        require("material", "You must specify a valid trim material!", Config::getString) {
            Registry.TRIM_MATERIAL.get(NamespacedKey.minecraft(it)) != null
        }
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val item = data.foundItem ?: return false
        val meta = item.itemMeta as? ArmorMeta ?: return false

        val material = Registry.TRIM_MATERIAL.get(NamespacedKey.minecraft(config.getString("material"))) ?: return false
        val pattern = Registry.TRIM_PATTERN.get(NamespacedKey.minecraft(config.getString("pattern"))) ?: return false

        meta.trim = ArmorTrim(material, pattern)
        item.itemMeta = meta

        return true
    }
}
