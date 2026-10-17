package com.willfp.libreforge.triggers

import java.util.concurrent.CopyOnWriteArrayList

/**
 * Every [TriggerOption], reloaded together with libreforge's config.yml.
 */
object TriggerOptions {
    private val options = CopyOnWriteArrayList<TriggerOption>()

    /**
     * Create a [TriggerOption] for the boolean at [path] in libreforge's config.yml.
     */
    fun create(path: String): TriggerOption {
        val option = TriggerOption(path)
        option.reload()
        options += option
        return option
    }

    internal fun reload() {
        for (option in options) {
            option.reload()
        }
    }
}
