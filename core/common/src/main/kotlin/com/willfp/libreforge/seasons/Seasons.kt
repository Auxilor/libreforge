package com.willfp.libreforge.seasons

import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.conditions.impl.ConditionIsSeason
import com.willfp.libreforge.dates.DateCalendar
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.filters.impl.FilterIsSeason
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.impl.TriggerSeasonEnd
import com.willfp.libreforge.triggers.impl.TriggerSeasonStart

/**
 * The seasons loaded from seasons.yml.
 */
object Seasons : DateCalendar<Season>("season", "seasons", ::Season) {
    override fun createCondition(id: String): Condition<*> = ConditionIsSeason(id)

    override fun createFilter(id: String): Filter<*, *> = FilterIsSeason(id)

    override fun createStartTrigger(id: String): Trigger = TriggerSeasonStart(id)

    override fun createEndTrigger(id: String): Trigger = TriggerSeasonEnd(id)
}
