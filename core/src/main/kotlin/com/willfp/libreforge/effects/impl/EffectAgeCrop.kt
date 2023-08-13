package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getIntFromExpression
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.block.data.Ageable

object EffectAgeCrop : Effect<NoCompileData>("age_crop") {
    override val parameters = setOf(
        TriggerParameter.BLOCK
    )

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val crop = data.block ?: data.location?.block ?: return false
        val state = crop.blockData as? Ageable ?: return false
        if (state.age == state.maximumAge) {
            return false
        }
        val age = if (config.has("age")) config.getIntFromExpression("age", data) else 1

        val newAge = (state.age + age).coerceAtMost(state.maximumAge)
        state.age = newAge
        crop.blockData = state

        return true
    }
}
