package com.willfp.libreforge.filters.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.entities.Entities
import com.willfp.eco.core.entities.TestableEntity
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.filters.EnumNames
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.filters.hasEnumName
import com.willfp.libreforge.triggers.TriggerData

object FilterEntities : Filter<Collection<TestableEntity>, List<String>>("entities") {
    override val description = "Matches when the victim entity type is in the given list."
    override val categories = setOf("entity")
    override val valueType = ArgType.ENTITY_LIST
    override val additionalInfo = listOf("Passes automatically when no victim is present in the trigger data.")

    override fun getValue(config: Config, data: TriggerData?, key: String): List<String> {
        return EnumNames(config.getStrings(key))
    }

    override fun isMet(data: TriggerData, value: List<String>, compileData: Collection<TestableEntity>): Boolean {
        val victim = data.victim ?: return true

        return value.hasEnumName(victim.type.name)
                || compileData.any { it.matches(victim) }
    }

    override fun makeCompileData(
        config: Config,
        context: ViolationContext,
        values: List<String>
    ): Collection<TestableEntity> {
        return values.map { Entities.lookup(it) }
    }
}
