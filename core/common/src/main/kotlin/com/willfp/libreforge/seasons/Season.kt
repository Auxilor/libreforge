package com.willfp.libreforge.seasons

import com.willfp.libreforge.dates.DateEntry
import com.willfp.libreforge.dates.DateRule

/**
 * A season defined in seasons.yml, e.g. meteorological winter or a monsoon season.
 */
class Season(id: String, rule: DateRule) : DateEntry(id, rule)
