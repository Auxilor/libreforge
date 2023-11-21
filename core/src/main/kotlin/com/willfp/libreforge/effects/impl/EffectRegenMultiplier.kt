package com.willfp.libreforge.effects.impl

import com.willfp.libreforge.effects.templates.MultiMultiplierEffect
import com.willfp.libreforge.enumValueOfOrNull
import com.willfp.libreforge.triggers.EntityDispatcher
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityRegainHealthEvent
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason

object EffectRegenMultiplier : MultiMultiplierEffect<RegainReason>("regen_multiplier") {
    override val key: String = "reason"

    override fun getElement(key: String): RegainReason? =
        enumValueOfOrNull<RegainReason>("reason")

    override fun getAllElements(): Collection<RegainReason> =
        RegainReason.values().toList()

    @EventHandler(ignoreCancelled = true)
    fun handle(event: EntityRegainHealthEvent) {
        event.amount *= getMultiplier(EntityDispatcher(event.entity), event.regainReason)
    }
}
