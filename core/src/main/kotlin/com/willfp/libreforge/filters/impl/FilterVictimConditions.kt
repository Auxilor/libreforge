package com.willfp.libreforge.filters.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.conditions.ConditionList
import com.willfp.libreforge.conditions.Conditions
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.EntityDispatcher
import com.willfp.libreforge.triggers.TriggerData

object FilterVictimConditions : Filter<ConditionList, Collection<Config>>("victim_conditions") {
    override fun getValue(config: Config, data: TriggerData?, key: String): Collection<Config> {
        return config.getSubsections(key)
    }

    override fun isMet(data: TriggerData, value: Collection<Config>, compileData: ConditionList): Boolean {
        val victim = data.victim ?: return false

        return compileData.areMet(EntityDispatcher(victim), data.holder)
    }

    override fun makeCompileData(config: Config, context: ViolationContext, values: Collection<Config>): ConditionList {
        return Conditions.compile(values, context.with("victim conditions"))
    }
}
