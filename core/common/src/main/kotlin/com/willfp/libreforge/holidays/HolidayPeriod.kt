package com.willfp.libreforge.holidays

import java.time.LocalDate
import java.time.Month
import java.time.ZoneId

/**
 * Multi-day holiday seasons, as inclusive date ranges. A period is keyed by the year it starts in,
 * so periods crossing New Year (e.g. the Twelve Days of Christmas) end in the following year.
 */
enum class HolidayPeriod(
    override val id: String,
    private val startRule: (year: Int) -> LocalDate,
    private val endRule: (year: Int) -> LocalDate
) : HolidayEntry {
    // Fourth Sunday before Christmas to Christmas Eve
    ADVENT("advent", { adventSunday(it) }, fixed(Month.DECEMBER, 24)),

    // Christmas Day to Twelfth Night (5 January)
    TWELVE_DAYS_OF_CHRISTMAS("twelve_days_of_christmas", fixed(Month.DECEMBER, 25), { LocalDate.of(it + 1, Month.JANUARY, 5) }),

    // Ash Wednesday to Holy Saturday
    LENT("lent", easter(-46), easter(-1)),

    // Palm Sunday to Holy Saturday
    HOLY_WEEK("holy_week", easter(-7), easter(-1)),

    // Easter Sunday to Pentecost
    EASTERTIDE("eastertide", easter(0), easter(49));

    /**
     * The first day of the period starting in [year].
     */
    fun startIn(year: Int): LocalDate = startRule(year)

    /**
     * The last day (inclusive) of the period starting in [year].
     */
    fun endIn(year: Int): LocalDate = endRule(year)

    /**
     * If [date] falls within this period.
     */
    override fun isOn(date: LocalDate): Boolean =
        (date.year - 1..date.year).any { !date.isBefore(startIn(it)) && !date.isAfter(endIn(it)) }

    override fun startsOn(date: LocalDate): Boolean = startIn(date.year) == date

    /**
     * If today falls within this period in [zone] (defaults to the zone set in config.yml).
     */
    @JvmOverloads
    fun isToday(zone: ZoneId = Holidays.zone): Boolean = isOn(LocalDate.now(zone))

    companion object {
        private val byId = entries.associateBy { it.id }

        /**
         * Get a period by its [id], or null if none match.
         */
        @JvmStatic
        fun getByID(id: String): HolidayPeriod? = byId[id.lowercase()]

        /**
         * All periods covering [date].
         */
        @JvmStatic
        fun on(date: LocalDate): List<HolidayPeriod> = entries.filter { it.isOn(date) }

        /**
         * All periods covering today in [zone] (defaults to the zone set in config.yml).
         */
        @JvmStatic
        @JvmOverloads
        fun today(zone: ZoneId = Holidays.zone): List<HolidayPeriod> = on(LocalDate.now(zone))
    }
}
