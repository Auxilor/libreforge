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
import org.bukkit.inventory.meta.trim.TrimMaterial
import org.bukkit.inventory.meta.trim.TrimPattern

object EffectSetArmorTrim: Effect<NoCompileData>("set_armor_trim") {
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
        val item = data.item ?: return false
        val itemMeta = item.itemMeta ?: return false
        val armorMeta = itemMeta as? ArmorMeta ?: return false

        val material: TrimMaterial? = Registry.TRIM_MATERIAL.get(NamespacedKey.minecraft(config.getString("material")))
        val pattern: TrimPattern? = Registry.TRIM_PATTERN.get(NamespacedKey.minecraft(config.getString("pattern")))

        if (material == null || pattern == null) {
            return false
        }

        armorMeta.trim = ArmorTrim(material, pattern)
        item.itemMeta = armorMeta

        return true
    }
}