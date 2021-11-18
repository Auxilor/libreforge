package com.willfp.libreforge.api

import com.willfp.eco.core.EcoPlugin
import com.willfp.libreforge.api.conditions.Conditions
import com.willfp.libreforge.internal.WatcherTriggers
import org.apache.commons.lang.StringUtils
import java.lang.IllegalStateException

class LibReforge(
    val plugin: EcoPlugin
) {
    private val defaultPackage = StringUtils.join(
        arrayOf("com", "willfp", "libreforge", "api"),
        "."
    )

    init {
        instance = this
        plugin.eventManager.registerListener(WatcherTriggers(plugin))
        for (condition in Conditions.values()) {
            plugin.eventManager.registerListener(condition)
        }

        if (this.javaClass.packageName == defaultPackage) {
            throw IllegalStateException("You must shade and relocate libreforge into your jar!")
        }
    }

    companion object {
        lateinit var instance: LibReforge
    }
}