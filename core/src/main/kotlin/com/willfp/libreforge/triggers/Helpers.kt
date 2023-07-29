package com.willfp.libreforge.triggers

import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Projectile

fun Entity.tryAsLivingEntity(): LivingEntity? {
    return when (this) {
        is LivingEntity -> this
        is Projectile -> this.shooter as? LivingEntity
        else -> null
    }
}
