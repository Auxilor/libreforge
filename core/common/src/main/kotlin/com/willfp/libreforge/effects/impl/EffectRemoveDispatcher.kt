package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.get
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player

object EffectRemoveDispatcher : Effect<NoCompileData>("remove_dispatcher") {
    override val isPermanent: Boolean
        get() = false

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val dispatcher = data.dispatcher.get<LivingEntity>() ?: return false

        if (dispatcher is Player) {
            return false
        }

        dispatcher.remove()

        return true
    }
}
