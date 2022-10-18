package com.willfp.libreforge.filters.filters

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.entities.Entities
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.triggers.TriggerData

object FilterProjectiles : Filter() {
    override fun passes(data: TriggerData, config: Config): Boolean {
        val projectile = data.projectile ?: return true

        return config.withInverse("projectiles", Config::getStrings) {
            val valid = it.map { lookup -> Entities.lookup(lookup) }
            valid.any { test -> test.matches(projectile) }
        }
    }
}
