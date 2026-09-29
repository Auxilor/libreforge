package com.willfp.libreforge.holidays

import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.Month
import java.time.ZoneId

/**
 * Holidays bound to a fixed date or a calculated rule (Easter offsets, Nth weekday of a month).
 *
 * Dates are the nominal day of the holiday, never a substitute/observed day shifted off a weekend.
 * Lunar calendar holidays (Lunar New Year, Diwali, Eid, Hanukkah, ...) are not included, as they
 * can't be computed reliably without a lunisolar calendar.
 */
enum class Holiday(
    override val id: String,
    private val rule: (year: Int) -> LocalDate
) : HolidayEntry {
    // Fixed dates
    NEW_YEARS_DAY("new_years_day", fixed(Month.JANUARY, 1)),
    EPIPHANY("epiphany", fixed(Month.JANUARY, 6)),
    GROUNDHOG_DAY("groundhog_day", fixed(Month.FEBRUARY, 2)),
    VALENTINES_DAY("valentines_day", fixed(Month.FEBRUARY, 14)),
    ST_DAVIDS_DAY("st_davids_day", fixed(Month.MARCH, 1)),
    INTERNATIONAL_WOMENS_DAY("international_womens_day", fixed(Month.MARCH, 8)),
    ST_PATRICKS_DAY("st_patricks_day", fixed(Month.MARCH, 17)),
    APRIL_FOOLS_DAY("april_fools_day", fixed(Month.APRIL, 1)),
    EARTH_DAY("earth_day", fixed(Month.APRIL, 22)),
    ST_GEORGES_DAY("st_georges_day", fixed(Month.APRIL, 23)),
    MAY_DAY("may_day", fixed(Month.MAY, 1)),
    CINCO_DE_MAYO("cinco_de_mayo", fixed(Month.MAY, 5)),
    JUNETEENTH("juneteenth", fixed(Month.JUNE, 19)),
    CANADA_DAY("canada_day", fixed(Month.JULY, 1)),
    INDEPENDENCE_DAY("independence_day", fixed(Month.JULY, 4)),
    BASTILLE_DAY("bastille_day", fixed(Month.JULY, 14)),
    HALLOWEEN("halloween", fixed(Month.OCTOBER, 31)),
    ALL_SAINTS_DAY("all_saints_day", fixed(Month.NOVEMBER, 1)),
    DAY_OF_THE_DEAD("day_of_the_dead", fixed(Month.NOVEMBER, 2)),
    GUY_FAWKES_NIGHT("guy_fawkes_night", fixed(Month.NOVEMBER, 5)),
    REMEMBRANCE_DAY("remembrance_day", fixed(Month.NOVEMBER, 11)),
    ST_ANDREWS_DAY("st_andrews_day", fixed(Month.NOVEMBER, 30)),
    CHRISTMAS_EVE("christmas_eve", fixed(Month.DECEMBER, 24)),
    CHRISTMAS_DAY("christmas_day", fixed(Month.DECEMBER, 25)),
    BOXING_DAY("boxing_day", fixed(Month.DECEMBER, 26)),
    NEW_YEARS_EVE("new_years_eve", fixed(Month.DECEMBER, 31)),

    // Easter offsets
    SHROVE_TUESDAY("shrove_tuesday", easter(-47)),
    ASH_WEDNESDAY("ash_wednesday", easter(-46)),
    MOTHERING_SUNDAY("mothering_sunday", easter(-21)),
    PALM_SUNDAY("palm_sunday", easter(-7)),
    GOOD_FRIDAY("good_friday", easter(-2)),
    EASTER_SUNDAY("easter_sunday", easter(0)),
    EASTER_MONDAY("easter_monday", easter(1)),
    ASCENSION_DAY("ascension_day", easter(39)),
    PENTECOST("pentecost", easter(49)),
    WHIT_MONDAY("whit_monday", easter(50)),
    CORPUS_CHRISTI("corpus_christi", easter(60)),

    // Nth weekday of a month (negative ordinal counts from the end)
    MARTIN_LUTHER_KING_DAY("martin_luther_king_day", weekday(Month.JANUARY, 3, DayOfWeek.MONDAY)),
    PRESIDENTS_DAY("presidents_day", weekday(Month.FEBRUARY, 3, DayOfWeek.MONDAY)),
    EARLY_MAY_BANK_HOLIDAY("early_may_bank_holiday", weekday(Month.MAY, 1, DayOfWeek.MONDAY)),
    MOTHERS_DAY("mothers_day", weekday(Month.MAY, 2, DayOfWeek.SUNDAY)),
    MEMORIAL_DAY("memorial_day", weekday(Month.MAY, -1, DayOfWeek.MONDAY)),
    SPRING_BANK_HOLIDAY("spring_bank_holiday", weekday(Month.MAY, -1, DayOfWeek.MONDAY)),
    FATHERS_DAY("fathers_day", weekday(Month.JUNE, 3, DayOfWeek.SUNDAY)),
    SUMMER_BANK_HOLIDAY("summer_bank_holiday", weekday(Month.AUGUST, -1, DayOfWeek.MONDAY)),
    LABOR_DAY("labor_day", weekday(Month.SEPTEMBER, 1, DayOfWeek.MONDAY)),
    COLUMBUS_DAY("columbus_day", weekday(Month.OCTOBER, 2, DayOfWeek.MONDAY)),
    CANADIAN_THANKSGIVING("canadian_thanksgiving", weekday(Month.OCTOBER, 2, DayOfWeek.MONDAY)),
    THANKSGIVING("thanksgiving", weekday(Month.NOVEMBER, 4, DayOfWeek.THURSDAY)),
    BLACK_FRIDAY("black_friday", { THANKSGIVING.dateIn(it).plusDays(1) }),
    CYBER_MONDAY("cyber_monday", { THANKSGIVING.dateIn(it).plusDays(4) });

    /**
     * The date this holiday falls on in [year].
     */
    fun dateIn(year: Int): LocalDate = rule(year)

    /**
     * If this holiday falls on [date].
     */
    override fun isOn(date: LocalDate): Boolean = dateIn(date.year) == date

    override fun startsOn(date: LocalDate): Boolean = isOn(date)

    /**
     * If this holiday is today in [zone] (defaults to the zone set in config.yml).
     */
    @JvmOverloads
    fun isToday(zone: ZoneId = Holidays.zone): Boolean = isOn(LocalDate.now(zone))

    companion object {
        private val byId = entries.associateBy { it.id }

        /**
         * Get a holiday by its [id], or null if none match.
         */
        @JvmStatic
        fun getByID(id: String): Holiday? = byId[id.lowercase()]

        /**
         * All holidays in [year], sorted by date.
         */
        @JvmStatic
        fun inYear(year: Int): List<Pair<Holiday, LocalDate>> =
            entries.map { it to it.dateIn(year) }.sortedBy { it.second }

        /**
         * All holidays falling on [date].
         */
        @JvmStatic
        fun on(date: LocalDate): List<Holiday> = entries.filter { it.isOn(date) }

        /**
         * All holidays falling on the date of [instant] in [zone] (defaults to the zone set in config.yml).
         */
        @JvmStatic
        @JvmOverloads
        fun on(instant: Instant, zone: ZoneId = Holidays.zone): List<Holiday> =
            on(LocalDate.ofInstant(instant, zone))

        /**
         * All holidays today in [zone] (defaults to the zone set in config.yml).
         */
        @JvmStatic
        @JvmOverloads
        fun today(zone: ZoneId = Holidays.zone): List<Holiday> = on(LocalDate.now(zone))
    }
}
