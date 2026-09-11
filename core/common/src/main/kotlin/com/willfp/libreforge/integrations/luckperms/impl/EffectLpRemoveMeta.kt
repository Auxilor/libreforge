package com.willfp.libreforge.integrations.luckperms.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.integrations.luckperms.dataFor
import com.willfp.libreforge.integrations.luckperms.modifyUser
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import net.luckperms.api.node.NodeType

object EffectLpRemoveMeta : Effect<NoCompileData>("lp_remove_meta") {
    override val description = "Removes a LuckPerms meta key from the player."
    override val categories = setOf("permission")
    override val additionalInfo = listOf(
        "Requires the LuckPerms plugin.",
        "Saves asynchronously, so the change is not visible immediately.",
        "Removes the meta in every context it was set in."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        require(
            "key",
            "You must specify the meta key!",
            description = "The LuckPerms meta key to remove.",
            type = ArgType.STRING,
            example = "rank-color"
        )
        optional(
            "transient",
            description = "If the meta should be removed from transient data instead of normal data.",
            type = ArgType.BOOLEAN,
            default = "false"
        )
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val player = data.player ?: return false
        val key = config.getString("key")

        return modifyUser(player) { user ->
            user.dataFor(config).clear { node ->
                NodeType.META.tryCast(node).map { it.metaKey.equals(key, ignoreCase = true) }.orElse(false)
            }
        }
    }
}
