package com.willfp.libreforge.filters.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.dot
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.toFloat3
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.xz

object FilterIsBehindVictim : Filter<NoCompileData, Boolean>("is_behind_victim") {
    override fun getValue(config: Config, data: TriggerData?, key: String): Boolean {
        return config.getBool(key)
    }

    override fun isMet(data: TriggerData, value: Boolean, compileData: NoCompileData): Boolean {
        val player = data.player ?: return true
        val victim = data.victim ?: return true

        val playerVector = player.location.direction.toFloat3().xz
        val victimVector = victim.location.direction.toFloat3().xz

        val isBehind = playerVector.dot(victimVector) < 0
        return isBehind == value
    }
}
