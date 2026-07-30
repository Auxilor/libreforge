package com.willfp.libreforge

import com.willfp.eco.core.Prerequisite
import com.willfp.eco.core.integrations.antigrief.AntigriefManager
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player

fun LivingEntity.dealDamage(
    amount: Double,
    source: Player? = null,
    trueDamage: Boolean = false,
    checkAntigrief: Boolean = true
): Boolean {
    if (source != null && source == this) {
        return false
    }

    if (checkAntigrief && source != null && !AntigriefManager.canInjure(source, this)) {
        return false
    }

    if (trueDamage) {
        if (amount >= this.health) {
            this.health = 0.0
            if (Prerequisite.HAS_PAPER.isMet) {
                this.killer = source
            }
        } else {
            this.health -= amount
        }

        return true
    }

    if (source != null) {
        this.damage(amount, source)
    } else {
        this.damage(amount)
    }

    return true
}
