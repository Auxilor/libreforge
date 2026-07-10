package com.willfp.libreforge.conditions.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.get
import org.bukkit.entity.Player


object ConditionHasPermission : Condition<NoCompileData>("has_permission") {
    override val description = "Passes when the player has the specified permission node."
    override val categories = setOf("permission")

    override val arguments = arguments {
        require(
            "permission",
            "You must specify the permission!",
            description = "The permission node to check.",
            type = ArgType.STRING,
            example = "myplugin.vip.access"
        )
    }

    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ): Boolean {
        val player = dispatcher.get<Player>() ?: return false

        return player.hasPermission(config.getString("permission"))
    }
}
