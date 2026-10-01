package com.willfp.libreforge.filters

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.dates.DateTimeMatcher
import com.willfp.libreforge.dates.Dates
import com.willfp.libreforge.dates.getStringOrStrings
import com.willfp.libreforge.triggers.TriggerData
import java.time.LocalDateTime

/**
 * A filter on the current date or time in [Dates.zone], compiled once into a [DateTimeMatcher].
 *
 * Values that can't be read are logged as violations, and the filter never matches.
 */
abstract class DateTimeFilter<V>(
    id: String
) : Filter<DateTimeMatcher?, V>(id) {
    override val categories = setOf("date")

    /**
     * Read [value], throwing [IllegalArgumentException] if it's invalid.
     */
    protected abstract fun compile(value: V): DateTimeMatcher

    override fun makeCompileData(config: Config, context: ViolationContext, values: V): DateTimeMatcher? {
        return try {
            compile(values)
        } catch (e: IllegalArgumentException) {
            context.log(this, ConfigViolation(id, e.message ?: "Invalid value"))
            null
        }
    }

    override fun isMet(data: TriggerData, value: V, compileData: DateTimeMatcher?): Boolean {
        return compileData?.matches(LocalDateTime.now(Dates.zone)) ?: false
    }

    /**
     * A filter whose value is a single string or a list of them.
     */
    abstract class Values(
        id: String
    ) : DateTimeFilter<List<String>>(id) {
        override val valueType = ArgType.STRING_LIST

        override fun getValue(config: Config, data: TriggerData?, key: String): List<String> {
            return config.getStringOrStrings(key)
        }
    }

    /**
     * A filter whose value is a section with `from` and `to`.
     */
    abstract class Between(
        id: String
    ) : DateTimeFilter<Config>(id) {
        override val valueType = ArgType.ANY

        override fun getValue(config: Config, data: TriggerData?, key: String): Config {
            return config.getSubsection(key)
        }

        protected abstract fun compile(from: String, to: String): DateTimeMatcher

        final override fun compile(value: Config): DateTimeMatcher =
            compile(value.getString("from"), value.getString("to"))
    }
}
