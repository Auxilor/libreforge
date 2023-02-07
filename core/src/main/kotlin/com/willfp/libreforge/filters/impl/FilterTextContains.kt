package com.willfp.libreforge.filters.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.triggers.TriggerData

object FilterTextContains : Filter() {
    override fun passes(data: TriggerData, config: Config): Boolean {
        val text = data.text ?: return true

        return config.withInverse("text_contains", Config::getStrings) {
            it.any { test -> text.lowercase().contains(test.lowercase()) }
        }
    }
}
