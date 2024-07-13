package com.willfp.libreforge.proxy

import org.bukkit.entity.EntityType
import org.bukkit.potion.PotionEffectType

@Proxy(location = "RenamedValuesImpl")
interface RenamedValues {
    val entities: RenamedEntities
    val potions: RenamedPotions
}

interface RenamedEntities {
    val mooshroom: EntityType
}

interface RenamedPotions {
    val slowness: PotionEffectType
    val resistance: PotionEffectType
}

fun renamedValues() = loadProxy(RenamedValues::class.java)
