package com.willfp.libreforge.conditions

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgumentMeta
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.dates.DateTimeMatcher
import com.willfp.libreforge.dates.Dates
import java.time.LocalDateTime

/**
 * A condition on the current date or time in [Dates.zone], compiled once into a [DateTimeMatcher].
 *
 * Args that can't be read are logged as violations, and the condition never passes.
 */
abstract class DateTimeCondition(
    id: String
) : Condition<DateTimeMatcher?>(id) {
    override val categories = setOf("date")

    override val additionalInfo = listOf(
        "The date and time are worked out in the timezone set by dates.timezone in config.yml."
    )

    /**
     * Read the args in [config], throwing [IllegalArgumentException] if they're invalid.
     */
    protected abstract fun compile(config: Config): DateTimeMatcher

    override fun makeCompileData(config: Config, context: ViolationContext): DateTimeMatcher? {
        return try {
            compile(config)
        } catch (e: IllegalArgumentException) {
            val param = arguments.docs.filterIsInstance<ArgumentMeta.Regular>().firstOrNull()?.names?.first() ?: id
            context.log(this, ConfigViolation(param, e.message ?: "Invalid value"))
            null
        }
    }

    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: DateTimeMatcher?
    ): Boolean {
        return compileData?.matches(LocalDateTime.now(Dates.zone)) ?: false
    }
}
