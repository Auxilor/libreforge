package com.willfp.libreforge.filters.filters

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.entities.Entities
import com.willfp.libreforge.filters.FilterComponent
import com.willfp.libreforge.triggers.TriggerData

class FilterProjectiles : FilterComponent() {
    override fun passes(data: TriggerData, config: Config): Boolean {
        val projectile = data.projectile ?: return true

        val valid = config.getStrings("projectiles").map { Entities.lookup(it) }

        return valid.any { it.matches(projectile) }
    }
}
