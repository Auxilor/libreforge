package com.willfp.libreforge.integrations.luckperms.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.get
import com.willfp.libreforge.integrations.luckperms.lpUser
import org.bukkit.entity.Player

object ConditionLpHasTemporaryNode : Condition<NoCompileData>("lp_has_temporary_node") {
    override val description = "Passes when the player has a temporary LuckPerms node with the specified key."
    override val categories = setOf("permission")
    override val additionalInfo = listOf(
        "Requires the LuckPerms plugin.",
        "Node keys are the raw keys, so a group node is group.vip rather than vip.",
        "Only checks nodes set directly on the player, not inherited ones."
    )

    override val arguments = arguments {
        require(
            "key",
            "You must specify the node key!",
            description = "The raw node key to look for, for example group.vip.",
            type = ArgType.STRING,
            example = "group.vip"
        )
    }

    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ): Boolean {
        val player = dispatcher.get<Player>() ?: return false
        val user = player.lpUser ?: return false
        val key = config.getString("key")

        return user.nodes.any { it.key.equals(key, ignoreCase = true) && it.hasExpiry() && !it.hasExpired() }
    }
}
