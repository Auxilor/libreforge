package com.willfp.libreforge.filters.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.conditions.ConditionList
import com.willfp.libreforge.conditions.Conditions
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.TriggerData

object FilterVictimConditions : Filter<ConditionList, Collection<Config>>("victim_conditions") {
    override val description = "Matches when the victim entity meets all of the given conditions."
    override val categories = setOf("entity")
    override val valueType = ArgType.ANY
    override val additionalInfo = listOf("Fails (does not pass) when no victim is present in the trigger data.")

    override fun getValue(config: Config, data: TriggerData?, key: String): Collection<Config> {
        return config.getSubsections(key)
    }

    override fun isMet(data: TriggerData, value: Collection<Config>, compileData: ConditionList): Boolean {
        val victim = data.victim ?: return false

        return compileData.areMet(victim.toDispatcher(), data.holder)
    }

    override fun makeCompileData(config: Config, context: ViolationContext, values: Collection<Config>): ConditionList {
        return Conditions.compile(values, context.with("victim conditions"))
    }
}
