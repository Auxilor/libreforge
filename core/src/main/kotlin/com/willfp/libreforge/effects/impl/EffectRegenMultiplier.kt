package com.willfp.libreforge.effects.impl

import com.willfp.libreforge.effects.templates.MultiMultiplierEffect
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityRegainHealthEvent
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason

object EffectRegenMultiplier : MultiMultiplierEffect<RegainReason>("regen_multiplier") {
    override val key: String = "reason"

    override fun getElement(key: String): RegainReason? =
        runCatching { enumValueOf<RegainReason>("reason") }.getOrNull()

    override fun getAllElements(): Collection<RegainReason> =
        RegainReason.values().toList()

    @EventHandler(ignoreCancelled = true)
    fun handle(event: EntityRegainHealthEvent) {
        val player = event.entity

        if (player !is Player) {
            return
        }

        event.amount *= getMultiplier(player, event.regainReason)
    }
}
