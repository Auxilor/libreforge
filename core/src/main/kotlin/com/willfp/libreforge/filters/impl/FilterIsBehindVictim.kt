package com.willfp.libreforge.filters.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.effects.effects.particles.toVector2f
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.triggers.TriggerData
import kotlin.math.roundToInt

object FilterIsBehindVictim : Filter<NoCompileData, Boolean>("is_behind_victim") {
    override fun getValue(config: Config, data: TriggerData?, key: String): Boolean {
        return config.getBool(key)
    }

    override fun isMet(data: TriggerData, value: Boolean, compileData: NoCompileData): Boolean {
        val player = data.player ?: return true
        val victim = data.victim ?: return true

        val playerVector = player.location.direction.toVector2f()
        val victimVector = victim.location.direction.toVector2f()

        val angle = Math.toDegrees(playerVector.angle(victimVector).toDouble()).roundToInt()
        val isBehind = angle in (-32..60) // Old code from Backstab from EE v8
        return isBehind == value
    }
}
