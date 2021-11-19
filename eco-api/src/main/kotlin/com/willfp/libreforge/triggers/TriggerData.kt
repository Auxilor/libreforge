package com.willfp.libreforge.triggers

import org.bukkit.Location
import org.bukkit.block.Block
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile

data class TriggerData(
    val player: Player? = null,
    val victim: LivingEntity? = null,
    val block: Block? = null,
    val event: WrappedEvent<*>? = null,
    val location: Location? = null,
    val projectile: Projectile? = null
)
