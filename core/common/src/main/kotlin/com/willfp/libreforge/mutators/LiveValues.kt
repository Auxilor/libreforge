package com.willfp.libreforge.mutators

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.DynamicNumericValue
import com.willfp.libreforge.mapToPlaceholders
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.placeholders.impl.TriggerPlaceholderAltValue
import com.willfp.libreforge.triggers.placeholders.impl.TriggerPlaceholderValue

/**
 * Re-inject the value placeholders into a mutator config using the values from [data].
 *
 * Trigger placeholders are injected into mutator configs once, before the mutator list runs,
 * so %value% and %altvalue% would otherwise always refer to the values from before any
 * mutators ran. Re-injecting them means that value expressions read the live values,
 * which lets several value mutators chain off each other.
 *
 * [DynamicNumericValue] is used rather than a plain NamedValue because the injected
 * placeholders have to hash differently per value, otherwise the cached expression result
 * from the previous mutation would be reused.
 */
internal fun Config.injectValuesFrom(data: TriggerData) {
    addInjectablePlaceholder(
        listOf(
            DynamicNumericValue(TriggerPlaceholderValue.identifiers, data.value),
            DynamicNumericValue(TriggerPlaceholderAltValue.identifiers, data.altValue)
        ).mapToPlaceholders()
    )
}
