package com.willfp.libreforge

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.PluginLike
import com.willfp.eco.core.registry.Registrable
import com.willfp.eco.core.registry.Registry
import com.willfp.libreforge.configs.LibreforgeConfigCategory

object Plugins : Registry<LibreforgePluginLike>()

interface LibreforgePluginLike : Registrable, PluginLike {
    val plugin: EcoPlugin

    val categories: Registry<LibreforgeConfigCategory>

    override fun getID() = plugin.id
}
