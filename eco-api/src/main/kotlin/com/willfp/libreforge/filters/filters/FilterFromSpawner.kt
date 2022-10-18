package com.willfp.libreforge.filters.filters

import com.willfp.eco.core.Prerequisite
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.triggers.TriggerData

object FilterFromSpawner : Filter() {
    override fun passes(data: TriggerData, config: Config): Boolean {
        val entity = data.victim ?: return true

        if (!Prerequisite.HAS_PAPER.isMet) {
            return true
        }

        return config.withInverse("from_spawner", Config::getBool) {
            it == entity.fromMobSpawner()
        }
    }
}
