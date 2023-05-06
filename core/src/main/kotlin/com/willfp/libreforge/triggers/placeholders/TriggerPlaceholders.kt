package com.willfp.libreforge.triggers.placeholders

import com.willfp.eco.core.registry.Registry
import com.willfp.libreforge.triggers.placeholders.impl.TriggerPlaceholderDistance
import com.willfp.libreforge.triggers.placeholders.impl.TriggerPlaceholderHits
import com.willfp.libreforge.triggers.placeholders.impl.TriggerPlaceholderLocation
import com.willfp.libreforge.triggers.placeholders.impl.TriggerPlaceholderText
import com.willfp.libreforge.triggers.placeholders.impl.TriggerPlaceholderValue
import com.willfp.libreforge.triggers.placeholders.impl.TriggerPlaceholderVictim

object TriggerPlaceholders : Registry<TriggerPlaceholder>() {
    init {
        register(TriggerPlaceholderDistance)
        register(TriggerPlaceholderHits)
        register(TriggerPlaceholderLocation)
        register(TriggerPlaceholderText)
        register(TriggerPlaceholderValue)
        register(TriggerPlaceholderVictim)
    }
}
