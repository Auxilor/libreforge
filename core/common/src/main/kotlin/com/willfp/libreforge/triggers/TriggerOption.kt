package com.willfp.libreforge.triggers

import com.willfp.libreforge.plugin

/**
 * A boolean in libreforge's config.yml that must be enabled for the triggers using it to
 * be listened to. Created with [TriggerOptions.create].
 *
 * The value is cached on reload, so checking [isEnabled] never reads the config.
 */
class TriggerOption internal constructor(
    val path: String
) {
    /**
     * If the option is enabled.
     */
    @Volatile
    var isEnabled: Boolean = false
        private set

    internal fun reload() {
        isEnabled = plugin.configYml.getBool(path)
    }
}
