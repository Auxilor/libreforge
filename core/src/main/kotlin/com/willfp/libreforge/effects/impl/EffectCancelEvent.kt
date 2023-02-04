package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.Triggers
import com.willfp.libreforge.triggers.WrappedCancellableEvent

class EffectCancelEvent : Effect(
    "cancel_event",
    triggers = Triggers.withParameters(
        TriggerParameter.EVENT
    ),
    noDelay = true
) {
    override fun handle(data: TriggerData, config: Config) {
        val event = data.event as? WrappedCancellableEvent<*> ?: return
        event.isCancelled = true
    }
}