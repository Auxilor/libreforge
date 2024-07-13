package com.willfp.libreforge.triggers

import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.holders
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

fun Dispatcher<*>.hasCondition(condition : Condition<*>) : Boolean {
    return this.holders.any { it.holder.conditions.any { it.condition.id == condition.id } || it.holder.effects.any { it.conditions.any { it.condition.id == condition.id } } }
}
