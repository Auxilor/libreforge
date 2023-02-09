package com.willfp.libreforge.filters.impl

import com.willfp.eco.core.Prerequisite
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.triggers.TriggerData

object FilterFromSpawner : Filter<NoCompileData, Boolean>("from_spawner") {
    override fun getValue(config: Config, data: TriggerData?, key: String): Boolean {
        return config.getBool(key)
    }

    override fun isMet(data: TriggerData, value: Boolean, compileData: NoCompileData): Boolean {
        val victim = data.victim ?: return true

        if (!Prerequisite.HAS_PAPER.isMet) {
            return true
        }

        return value == victim.fromMobSpawner()
    }
}
