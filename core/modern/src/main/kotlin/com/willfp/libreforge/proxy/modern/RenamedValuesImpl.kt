package com.willfp.libreforge.proxy.modern

import com.willfp.libreforge.proxy.RenamedEntities
import com.willfp.libreforge.proxy.RenamedPotions
import com.willfp.libreforge.proxy.RenamedValues
import org.bukkit.entity.EntityType
import org.bukkit.potion.PotionEffectType

class RenamedValuesImpl: RenamedValues {
    override val entities = object : RenamedEntities {
        override val mooshroom = EntityType.MOOSHROOM
    }

    override val potions = object : RenamedPotions {
        override val slowness = PotionEffectType.SLOWNESS
        override val resistance = PotionEffectType.RESISTANCE
    }
}
