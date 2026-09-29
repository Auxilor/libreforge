package com.willfp.libreforge.holidays

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.placeholder.context.PlaceholderContext
import com.willfp.eco.util.evaluateExpressionOrNull
import com.willfp.libreforge.conditions.Conditions
import com.willfp.libreforge.conditions.impl.ConditionIsHoliday
import com.willfp.libreforge.filters.Filters
import com.willfp.libreforge.filters.impl.FilterIsHoliday
import com.willfp.libreforge.plugin
import com.willfp.libreforge.triggers.Triggers
import com.willfp.libreforge.triggers.impl.TriggerHolidayStart
import java.time.DateTimeException
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.concurrent.ConcurrentHashMap

/**
 * The holidays loaded from holidays.yml, plus shared holiday settings and calculations.
 */
object Holidays {
    private var cachedZone: Pair<String, ZoneId>? = null

    @Volatile
    private var byId: Map<String, Holiday> = emptyMap()

    private val components = mutableMapOf<String, HolidayComponents>()

    private val easterCache = ConcurrentHashMap<Int, LocalDate>()

    private class HolidayComponents(
        val condition: ConditionIsHoliday,
        val filter: FilterIsHoliday,
        val trigger: TriggerHolidayStart
    )

    /**
     * Get a holiday by its [id], or null if none match.
     */
    @JvmStatic
    fun getByID(id: String): Holiday? = byId[id.lowercase()]

    /**
     * Every loaded holiday, in config order.
     */
    @JvmStatic
    fun values(): List<Holiday> = byId.values.toList()

    /**
     * All holidays on [date].
     */
    @JvmStatic
    fun on(date: LocalDate): List<Holiday> = byId.values.filter { it.isOn(date) }

    /**
     * All holidays on the date of [instant] in [zone] (defaults to the zone set in config.yml).
     */
    @JvmStatic
    @JvmOverloads
    fun on(instant: Instant, zone: ZoneId = this.zone): List<Holiday> =
        on(LocalDate.ofInstant(instant, zone))

    /**
     * All holidays today in [zone] (defaults to the zone set in config.yml).
     */
    @JvmStatic
    @JvmOverloads
    fun today(zone: ZoneId = this.zone): List<Holiday> = on(LocalDate.now(zone))

    /**
     * Load holidays from [config] (holidays.yml), registering an `is_<id>` condition and filter
     * and an `<id>_start` trigger for each, and removing those of holidays no longer defined.
     */
    internal fun reload(config: Config) {
        val definitions = config.getSubsections("holidays").map {
            HolidayDefinition(
                id = it.getString("id"),
                active = it.getStringOrNull("active"),
                dates = it.getStringsOrNull("dates"),
                from = it.getStringOrNull("from"),
                to = it.getStringOrNull("to")
            )
        }

        val compiler = HolidayCompiler(
            evaluate = { evaluateExpressionOrNull(it, PlaceholderContext.EMPTY) },
            warn = { plugin.logger.warning("holidays.yml: $it") }
        )

        val holidays = compiler.compile(definitions)
        byId = holidays.associateBy { it.id }
        syncComponents(holidays.map { it.id }.toSet())
    }

    private fun syncComponents(ids: Set<String>) {
        for (id in components.keys - ids) {
            val removed = components.remove(id) ?: continue
            Conditions.remove(removed.condition)
            Filters.remove(removed.filter)
            Triggers.remove(removed.trigger)
        }

        for (id in ids - components.keys) {
            val holidayComponents = HolidayComponents(
                ConditionIsHoliday(id),
                FilterIsHoliday(id),
                TriggerHolidayStart(id)
            )

            val clashes = listOfNotNull(
                holidayComponents.condition.id.takeIf { Conditions.values().any { it.id == holidayComponents.condition.id } },
                holidayComponents.filter.id.takeIf { Filters.values().any { it.id == holidayComponents.filter.id } },
                holidayComponents.trigger.id.takeIf { Triggers.values().any { it.id == holidayComponents.trigger.id } }
            )

            if (clashes.isNotEmpty()) {
                plugin.logger.warning("holidays.yml: Holiday '$id' clashes with existing ${clashes.joinToString()}, skipping it")
                continue
            }

            Conditions.register(holidayComponents.condition)
            Filters.register(holidayComponents.filter)
            Triggers.register(holidayComponents.trigger)
            components[id] = holidayComponents
        }
    }

    /**
     * Fire the start trigger of every holiday beginning on [date].
     */
    internal fun dispatchStarts(date: LocalDate) {
        for ((id, holidayComponents) in components.toMap()) {
            if (getByID(id)?.startsOn(date) == true) {
                holidayComponents.trigger.dispatchForOnlinePlayers()
            }
        }
    }

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
    fun easterSunday(year: Int): LocalDate = easterCache.computeIfAbsent(year) { calculateEasterSunday(it) }

    private fun calculateEasterSunday(year: Int): LocalDate {
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
