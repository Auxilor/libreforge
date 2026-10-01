package com.willfp.libreforge.holidays

import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.conditions.impl.ConditionIsHoliday
import com.willfp.libreforge.dates.DateCalendar
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.filters.impl.FilterIsHoliday
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.impl.TriggerHolidayStart

/**
 * The holidays loaded from holidays.yml.
 */
object Holidays : DateCalendar<Holiday>("holiday", "holidays", ::Holiday) {
    override fun createCondition(id: String): Condition<*> = ConditionIsHoliday(id)

    override fun createFilter(id: String): Filter<*, *> = FilterIsHoliday(id)

    override fun createTrigger(id: String): Trigger = TriggerHolidayStart(id)
}
