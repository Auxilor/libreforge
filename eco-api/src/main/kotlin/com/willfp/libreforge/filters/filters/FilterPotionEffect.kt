package com.willfp.libreforge.filters.filters

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.containsIgnoreCase
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.triggers.GenericCancellableEvent
import com.willfp.libreforge.triggers.TriggerData
import org.bukkit.event.entity.EntityPotionEffectEvent

object FilterPotionEffect: Filter() {
    override fun passes(data: TriggerData, config: Config): Boolean {
        val event = (data.event as? GenericCancellableEvent)?.handle as? EntityPotionEffectEvent ?: return true

        return config.withInverse("potion_effect", Config::getStrings) {
            it.containsIgnoreCase(event.newEffect?.type?.name?: "")
        }
    }
}
