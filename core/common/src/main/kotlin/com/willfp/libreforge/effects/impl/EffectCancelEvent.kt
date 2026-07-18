package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.effects.RunOrder
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.Cancellable

object EffectCancelEvent : Effect<NoCompileData>("cancel_event") {
    override val description = "Cancels the triggering event."
    override val categories = setOf("meta")
    override val additionalInfo = listOf("Requires a trigger that provides a cancellable EVENT.")

    override val supportsDelay = false

    override val runOrder = RunOrder.START

    override val parameters = setOf(
        TriggerParameter.EVENT
    )

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val event = data.event as? Cancellable ?: return false
        event.isCancelled = true

        return true
    }
}
