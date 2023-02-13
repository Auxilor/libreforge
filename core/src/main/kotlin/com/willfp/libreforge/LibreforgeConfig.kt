package com.willfp.libreforge

import com.willfp.eco.core.config.emptyConfig
import com.willfp.eco.core.config.interfaces.Config

object LibreforgeConfig : Config by emptyConfig() {
    fun getMessage(key: String) = getFormattedString("messages.$key")
}
