package com.willfp.libreforge.commands.custom

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.registry.Registry

object CustomCommands : Registry<CustomCommand>() {
    fun load(config: Config, plugin: EcoPlugin) {
        val id = config.getString("id")
        val alias = config.getString("alias")
        val permission = config.getStringOrNull("permission")

        register(CustomCommand(id, alias, permission, plugin, config))
    }

    fun clearAndUnregister() {
        values().forEach { it.onRemove() }
        clear()
    }
}