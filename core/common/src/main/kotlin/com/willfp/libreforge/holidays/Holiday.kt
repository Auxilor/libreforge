package com.willfp.libreforge.holidays

import com.willfp.libreforge.dates.DateEntry
import com.willfp.libreforge.dates.DateRule

/**
 * A holiday or holiday period defined in holidays.yml.
 */
class Holiday(id: String, rule: DateRule) : DateEntry(id, rule)
