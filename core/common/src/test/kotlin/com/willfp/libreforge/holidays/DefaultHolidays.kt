package com.willfp.libreforge.holidays

import com.willfp.libreforge.dates.DefaultCalendar

/**
 * The holidays from the bundled holidays.yml.
 */
object DefaultHolidays : DefaultCalendar<Holiday>("holidays", "holiday", ::Holiday)
