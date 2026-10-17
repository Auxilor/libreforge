package com.willfp.libreforge.earning.placeholder

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.placeholder.context.PlaceholderContext
import com.willfp.eco.core.placeholder.templates.DynamicPlaceholder
import com.willfp.eco.util.toNiceString
import com.willfp.libreforge.earning.EarningLimits
import java.util.regex.Pattern

class EarningLimitPlaceholder(
    plugin: EcoPlugin
) : DynamicPlaceholder(plugin, Pattern.compile("earning_limit_(earned|remaining|reset_seconds)_([a-z0-9_-]+)")) {
    override fun getValue(args: String, context: PlaceholderContext): String? {
        val player = context.player ?: return null
        val matcher = pattern.matcher(args)

        if (!matcher.matches()) {
            return null
        }

        val status = EarningLimits.status(player, matcher.group(2))

        return when (matcher.group(1)) {
            "earned" -> status?.earned?.toNiceString() ?: "0"
            "remaining" -> status?.remaining?.toNiceString() ?: "0"
            else -> status?.remainingSeconds?.toString() ?: "-1"
        }
    }
}
