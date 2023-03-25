package com.willfp.libreforge.loader.internal

import com.willfp.eco.core.config.updating.ConfigHandler
import com.willfp.libreforge.LoadedLibreforgePlugin
import com.willfp.libreforge.loader.LibreforgePlugin
import java.io.File
import java.util.logging.Logger

internal class LoadedLibreforgePluginImpl(
    override val plugin: LibreforgePlugin
) : LoadedLibreforgePlugin {
    override val categories = plugin.categories
    override fun getID() = plugin.id
    override fun getConfigHandler(): ConfigHandler = plugin.configHandler
    override fun getDataFolder(): File = plugin.dataFolder
    override fun getLogger(): Logger = plugin.logger
}
