package com.willfp.libreforge.triggers

import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Projectile

/**
 * Attempts to cast the current entity to a LivingEntity.
 * If the entity is already a LivingEntity, it will be returned as is.
 * If the entity is a Projectile, its shooter will be cast to a LivingEntity, if possible.
 * Otherwise, returns null.
 *
 * @return The entity as a LivingEntity, or null if it cannot be cast to one.
 */
fun Entity.tryAsLivingEntity(): LivingEntity? = when (this) {
    is LivingEntity -> this
    is Projectile -> this.shooter as? LivingEntity
    else -> null
}
