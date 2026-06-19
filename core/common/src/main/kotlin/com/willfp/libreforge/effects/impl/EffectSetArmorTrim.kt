package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
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
    override val description = "Applies an armor trim with the specified pattern and material to the triggering item."
    override val categories = setOf("inventory")

    override val parameters = setOf(
        TriggerParameter.ITEM
    )

    override val arguments = arguments {
        require("pattern", "You must specify a valid trim pattern!", Config::getString) {
            @Suppress("DEPRECATION")
            Registry.TRIM_PATTERN.get(NamespacedKey.minecraft(it)) != null
        }
        describe(
            "pattern",
            description = "The trim pattern to apply, e.g. 'sentry' or 'dune'.",
            type = ArgType.STRING
        )
        require("material", "You must specify a valid trim material!", Config::getString) {
            @Suppress("DEPRECATION")
            Registry.TRIM_MATERIAL.get(NamespacedKey.minecraft(it)) != null
        }
        describe(
            "material",
            description = "The trim material to apply, e.g. 'gold' or 'diamond'.",
            type = ArgType.STRING
        )
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val item = data.foundItem ?: return false
        val meta = item.itemMeta as? ArmorMeta ?: return false

        @Suppress("DEPRECATION")
        val material = Registry.TRIM_MATERIAL.get(NamespacedKey.minecraft(config.getString("material"))) ?: return false
        @Suppress("DEPRECATION")
        val pattern = Registry.TRIM_PATTERN.get(NamespacedKey.minecraft(config.getString("pattern"))) ?: return false

        meta.trim = ArmorTrim(material, pattern)
        item.itemMeta = meta

        return true
    }
}
