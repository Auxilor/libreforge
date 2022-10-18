package com.willfp.libreforge.effects

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
