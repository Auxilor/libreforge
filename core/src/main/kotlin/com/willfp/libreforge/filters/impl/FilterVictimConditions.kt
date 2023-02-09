package com.willfp.libreforge.filters.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.conditions.ConditionList
import com.willfp.libreforge.conditions.Conditions
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.triggers.TriggerData
import org.bukkit.entity.Player

object FilterVictimConditions : Filter<ConditionList, Collection<Config>>("victim_conditions") {
    override fun getValue(config: Config, data: TriggerData?, key: String): Collection<Config> {
        return config.getSubsections(key)
    }

    override fun isMet(data: TriggerData, value: Collection<Config>, compileData: ConditionList): Boolean {
        val victim = data.victim as? Player ?: return true

        return compileData.areMet(victim)
    }

    override fun makeCompileData(config: Config, context: ViolationContext, values: Collection<Config>): ConditionList {
        return Conditions.compile(values, context.with("victim conditions"))
    }
}
