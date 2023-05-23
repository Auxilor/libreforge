package com.willfp.libreforge.levels.placeholder

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.placeholder.RegistrablePlaceholder
import com.willfp.eco.core.placeholder.context.PlaceholderContext
import com.willfp.eco.util.toNiceString
import com.willfp.eco.util.toNumeral
import java.util.regex.Pattern

abstract class ItemPointPlaceholder(
    private val plugin: EcoPlugin,
    category: String
) : RegistrablePlaceholder {
    private val pattern = Regex("item_${category}_[a-zA-Z0-9_-]+").toPattern()
    private val prefix = "item_${category}_"

    override fun getValue(args: String, context: PlaceholderContext): String? {
        val isNumeral = args.endsWith("_numeral")
        val pointName = args.removePrefix(prefix).removeSuffix("_numeral")

        return getValue(context, pointName)?.let {
            if (it == Double.MAX_VALUE) {
                return plugin.langYml.getString("infinity")
            }

            if (isNumeral) {
                it.toInt().toNumeral()
            } else {
                it.toNiceString()
            }
        }
    }

    override fun getPattern(): Pattern {
        return pattern
    }

    override fun getPlugin(): EcoPlugin {
        return plugin
    }

    abstract fun getValue(context: PlaceholderContext, type: String): Double?
}
