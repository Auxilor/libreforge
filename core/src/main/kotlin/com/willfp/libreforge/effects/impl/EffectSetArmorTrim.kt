package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.inventory.meta.ArmorMeta
import org.bukkit.inventory.meta.trim.ArmorTrim
import org.bukkit.inventory.meta.trim.TrimMaterial
import org.bukkit.inventory.meta.trim.TrimPattern

object EffectSetArmorTrim: Effect<NoCompileData>("set_armor_trim") {
    override val parameters = setOf(
        TriggerParameter.ITEM
    )

    override val arguments = arguments {
        require("pattern", "You must specify the trim pattern!")
        require("material", "You must specify the trim material!")
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val item = data.item ?: return false
        val itemMeta = item.itemMeta ?: return false
        val armorMeta = itemMeta as? ArmorMeta ?: return false

        var pattern = TrimPattern.COAST
        when (config.getString("pattern").lowercase()) {
            "dune" -> pattern = TrimPattern.DUNE
            "eye" -> pattern = TrimPattern.EYE
            "host" -> pattern = TrimPattern.HOST
            "raiser" -> pattern = TrimPattern.RAISER
            "rib" -> pattern = TrimPattern.RIB
            "sentry" -> pattern = TrimPattern.SENTRY
            "shaper" -> pattern = TrimPattern.SHAPER
            "silence" -> pattern = TrimPattern.SILENCE
            "snout" -> pattern = TrimPattern.SNOUT
            "spire" -> pattern = TrimPattern.SPIRE
            "tide" -> pattern = TrimPattern.TIDE
            "vex" -> pattern = TrimPattern.VEX
            "ward" -> pattern = TrimPattern.WARD
            "wayfinder" -> pattern = TrimPattern.WAYFINDER
            "wild" -> pattern = TrimPattern.WILD
        }
        var material = TrimMaterial.AMETHYST
        when (config.getString("material").lowercase()) {
            "copper" -> material = TrimMaterial.COPPER
            "diamond" -> material = TrimMaterial.DIAMOND
            "emerald" -> material = TrimMaterial.EMERALD
            "gold" -> material = TrimMaterial.GOLD
            "iron" -> material = TrimMaterial.IRON
            "lapis" -> material = TrimMaterial.LAPIS
            "netherite" -> material = TrimMaterial.NETHERITE
            "quartz" -> material = TrimMaterial.QUARTZ
            "redstone" -> material = TrimMaterial.REDSTONE
        }

        val trim = ArmorTrim(material, pattern)
        armorMeta.trim = trim
        item.itemMeta = armorMeta

        return true
    }
}