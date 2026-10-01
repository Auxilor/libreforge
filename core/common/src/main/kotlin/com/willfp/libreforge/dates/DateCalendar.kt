package com.willfp.libreforge.dates

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.placeholder.context.PlaceholderContext
import com.willfp.eco.util.evaluateExpressionOrNull
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.conditions.Conditions
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.filters.Filters
import com.willfp.libreforge.plugin
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.Triggers
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

/**
 * A set of [DateEntry]s loaded from a config file, each of which gets an `is_<id>` condition
 * and filter and `<id>_start` and `<id>_end` triggers.
 */
abstract class DateCalendar<T : DateEntry>(
    /**
     * What a single entry is called, e.g. `holiday`.
     */
    val kind: String,

    /**
     * The config file name (without `.yml`), which is also the key of its list, e.g. `holidays`.
     */
    val fileName: String,

    private val create: (id: String, rule: DateRule) -> T
) {
    @Volatile
    private var byId: Map<String, T> = emptyMap()

    private val components = mutableMapOf<String, DateComponents>()

    private class DateComponents(
        val condition: Condition<*>,
        val filter: Filter<*, *>,
        val startTrigger: Trigger,
        val endTrigger: Trigger
    ) {
        val triggers: List<Trigger>
            get() = listOf(startTrigger, endTrigger)
    }

    /**
     * The `is_<id>` condition for the entry [id].
     */
    protected abstract fun createCondition(id: String): Condition<*>

    /**
     * The `is_<id>` filter for the entry [id].
     */
    protected abstract fun createFilter(id: String): Filter<*, *>

    /**
     * The `<id>_start` trigger for the entry [id], fired for every online player when it begins.
     */
    protected abstract fun createStartTrigger(id: String): Trigger

    /**
     * The `<id>_end` trigger for the entry [id], fired for every online player in the last minute of it.
     */
    protected abstract fun createEndTrigger(id: String): Trigger

    /**
     * Get an entry by its [id], or null if none match.
     */
    fun getByID(id: String): T? = byId[id.lowercase()]

    /**
     * Every loaded entry, in config order.
     */
    fun values(): List<T> = byId.values.toList()

    /**
     * All entries on [date].
     */
    fun on(date: LocalDate): List<T> = byId.values.filter { it.isOn(date) }

    /**
     * All entries on the date of [instant] in [zone] (defaults to the zone set in config.yml).
     */
    @JvmOverloads
    fun on(instant: Instant, zone: ZoneId = Dates.zone): List<T> =
        on(LocalDate.ofInstant(instant, zone))

    /**
     * All entries today in [zone] (defaults to the zone set in config.yml).
     */
    @JvmOverloads
    fun today(zone: ZoneId = Dates.zone): List<T> = on(LocalDate.now(zone))

    /**
     * Load entries from [config], registering the components of each, and removing those of
     * entries no longer defined.
     */
    internal fun reload(config: Config) {
        val definitions = config.getSubsections(fileName).map {
            DateDefinition(
                id = it.getString("id"),
                active = it.getStringOrNull("active"),
                dates = it.getStringsOrNull("dates"),
                from = it.getStringOrNull("from"),
                to = it.getStringOrNull("to")
            )
        }

        val compiler = DateCompiler(
            kind = kind,
            create = create,
            evaluate = { evaluateExpressionOrNull(it, PlaceholderContext.EMPTY) },
            warn = { plugin.logger.warning("$fileName.yml: $it") }
        )

        val entries = compiler.compile(definitions)
        byId = entries.associateBy { it.id }
        syncComponents(entries.map { it.id }.toSet())
    }

    private fun syncComponents(ids: Set<String>) {
        for (id in components.keys - ids) {
            val removed = components.remove(id) ?: continue
            Conditions.remove(removed.condition)
            Filters.remove(removed.filter)
            removed.triggers.forEach { Triggers.remove(it) }
        }

        for (id in ids - components.keys) {
            val dateComponents = DateComponents(
                createCondition(id),
                createFilter(id),
                createStartTrigger(id),
                createEndTrigger(id)
            )

            val clashes = listOfNotNull(
                dateComponents.condition.id.takeIf { Conditions.values().any { it.id == dateComponents.condition.id } },
                dateComponents.filter.id.takeIf { Filters.values().any { it.id == dateComponents.filter.id } }
            ) + dateComponents.triggers
                .map { it.id }
                .filter { triggerId -> Triggers.values().any { it.id == triggerId } }

            if (clashes.isNotEmpty()) {
                plugin.logger.warning("$fileName.yml: ${kind.replaceFirstChar { it.uppercase() }} '$id' clashes with existing ${clashes.joinToString()}, skipping it")
                continue
            }

            Conditions.register(dateComponents.condition)
            Filters.register(dateComponents.filter)
            dateComponents.triggers.forEach { Triggers.register(it) }
            components[id] = dateComponents
        }
    }

    /**
     * Fire the start trigger of every entry beginning on [date].
     */
    internal fun dispatchStarts(date: LocalDate) {
        for ((id, dateComponents) in components.toMap()) {
            if (getByID(id)?.startsOn(date) == true) {
                dateComponents.startTrigger.dispatchForOnlinePlayers()
            }
        }
    }

    /**
     * Fire the end trigger of every entry finishing on [date].
     */
    internal fun dispatchEnds(date: LocalDate) {
        for ((id, dateComponents) in components.toMap()) {
            if (getByID(id)?.endsOn(date) == true) {
                dateComponents.endTrigger.dispatchForOnlinePlayers()
            }
        }
    }
}
