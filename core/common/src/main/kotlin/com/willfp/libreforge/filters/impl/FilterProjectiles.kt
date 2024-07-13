package com.willfp.libreforge.filters.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.entities.Entities
import com.willfp.eco.core.entities.TestableEntity
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.triggers.TriggerData

object FilterProjectiles : Filter<Collection<TestableEntity>, List<String>>("projectiles") {
    override fun getValue(config: Config, data: TriggerData?, key: String): List<String> {
        return config.getStrings(key)
    }

    override fun isMet(data: TriggerData, value: List<String>, compileData: Collection<TestableEntity>): Boolean {
        val projectile = data.projectile
        return compileData.any { test -> test.matches(projectile) }
    }

    override fun makeCompileData(
        config: Config,
        context: ViolationContext,
        values: List<String>
    ): Collection<TestableEntity> {
        return values.map { lookup -> Entities.lookup(lookup) }
    }
}
