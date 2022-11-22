package com.willfp.libreforge.filters.filters

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.effects.effects.particles.toVector2f
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.triggers.TriggerData
import kotlin.math.roundToInt

object FilterIsNPC : Filter() {
    override fun passes(data: TriggerData, config: Config): Boolean {
        val victim = data.victim ?: return true

        return config.withInverse("is_npc", Config::getBool) {
            val isNPC = victim.hasMetadata("NPC")
            isNPC == it
        }
    }
}
