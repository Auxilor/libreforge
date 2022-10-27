package com.willfp.libreforge.filters.filters

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.effects.effects.particles.toVector2f
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.triggers.TriggerData
import kotlin.math.roundToInt

object FilterIsBehindVictim : Filter() {
    override fun passes(data: TriggerData, config: Config): Boolean {
        val player = data.player ?: return true
        val victim = data.victim ?: return true

        return config.withInverse("is_behind_victim", Config::getBool) {
            val playerVector = player.location.direction.toVector2f()
            val victimVector = victim.location.direction.toVector2f()

            val angle = Math.toDegrees(playerVector.angle(victimVector).toDouble()).roundToInt()
            val isBehind = angle in (-32..60) // Old code from Backstab from EE v8
            isBehind == it
        }
    }
}
