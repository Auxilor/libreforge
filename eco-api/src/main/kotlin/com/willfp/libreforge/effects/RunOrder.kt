package com.willfp.libreforge.effects

/**
 * Requires an effect to run after other effects on the same trigger, if
 * possible.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class RunsAt(
    val at: RunOrder
)

enum class RunOrder {
    /** Run absolutely first. Only use in exceptional circumstances. */
    START,

    /** Run early. */
    EARLY,

    /** Run in the middle (default) */
    NORMAL,

    /** Run late. */
    LATE,

    /** Run right at the end. Only use in exceptional circumstances. */
    END
}
