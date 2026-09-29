package com.willfp.libreforge.holidays

import com.willfp.libreforge.plugin
import java.time.DateTimeException
import java.time.LocalDate
import java.time.ZoneId

/**
 * Shared holiday settings and calculations.
 */
object Holidays {
    private var cachedZone: Pair<String, ZoneId>? = null

    /**
     * The zone holidays are evaluated in, from `holidays.timezone` in config.yml.
     * Blank or invalid values fall back to the server's system zone.
     */
    @JvmStatic
    val zone: ZoneId
        get() {
            val raw = plugin.configYml.getString("holidays.timezone").trim()
            cachedZone?.let { (cachedRaw, zone) -> if (cachedRaw == raw) return zone }

            val zone = if (raw.isEmpty()) {
                ZoneId.systemDefault()
            } else {
                try {
                    ZoneId.of(raw)
                } catch (e: DateTimeException) {
                    plugin.logger.warning("Invalid timezone for holidays.timezone: $raw, using system default")
                    ZoneId.systemDefault()
                }
            }

            cachedZone = raw to zone
            return zone
        }

    /**
     * Easter Sunday in [year] (Gregorian), via the anonymous Gregorian algorithm (Meeus/Jones/Butcher).
     */
    @JvmStatic
    fun easterSunday(year: Int): LocalDate {
        // Position of the year in the 19-year Metonic (lunar) cycle
        val metonicYear = year % 19

        val century = year / 100
        val yearOfCentury = year % 100

        // Gregorian leap-year corrections at the century level
        val centuryLeapDays = century / 4
        val centuryLeapRemainder = century % 4

        // Correction for the drift of the lunar orbit against the calendar
        val lunarDriftCorrection = (century - (century + 8) / 25 + 1) / 3

        // Days from 21 March to the Paschal full moon
        val daysToFullMoon = (19 * metonicYear + century - centuryLeapDays - lunarDriftCorrection + 15) % 30

        // Gregorian leap-year corrections within the century
        val yearLeapDays = yearOfCentury / 4
        val yearLeapRemainder = yearOfCentury % 4

        // Days from the Paschal full moon to the following Sunday
        val daysToSunday = (32 + 2 * centuryLeapRemainder + 2 * yearLeapDays - daysToFullMoon - yearLeapRemainder) % 7

        // Pulls Easter back a week in the rare years it would otherwise land too late
        val lateEasterCorrection = (metonicYear + 11 * daysToFullMoon + 22 * daysToSunday) / 451

        val daysFromMarchStart = daysToFullMoon + daysToSunday - 7 * lateEasterCorrection + 114
        val month = daysFromMarchStart / 31
        val day = daysFromMarchStart % 31 + 1

        return LocalDate.of(year, month, day)
    }
}
