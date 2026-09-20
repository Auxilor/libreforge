package com.willfp.libreforge.integrations.luckperms.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.get
import com.willfp.libreforge.integrations.luckperms.lpContexts
import org.bukkit.entity.Player

object ConditionLpHasContext : Condition<NoCompileData>("lp_has_context") {
    override val description = "Passes when the specified LuckPerms context is active for the player."
    override val categories = setOf("permission")
    override val additionalInfo = listOf(
        "Requires the LuckPerms plugin.",
        "Specify the value to require a specific context value, or omit it to require the key to be present."
    )

    override val arguments = arguments {
        require(
            "key",
            "You must specify the context key!",
            description = "The LuckPerms context key, for example server, world, or gamemode.",
            type = ArgType.STRING,
            example = "world"
        )
        optional(
            "value",
            description = "The value the context must have. If not specified, any value passes.",
            type = ArgType.STRING,
            example = "world_nether"
        )
    }

    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ): Boolean {
        val player = dispatcher.get<Player>() ?: return false
        val contexts = player.lpContexts
        val key = config.getString("key")
        val value = config.getStringOrNull("value") ?: return contexts.containsKey(key)

        return contexts.contains(key, value)
    }
}
