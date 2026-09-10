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
import org.bukkit.entity.Player

object EffectLpRemovePermission : Effect<NoCompileData>("lp_remove_permission") {
    override val description = "Unsets a permission from the player in LuckPerms."
    override val categories = setOf("permission")
    override val additionalInfo = listOf(
        "Requires the LuckPerms plugin.",
        "Saves asynchronously, so the change is not visible immediately.",
        "Removes the permission in every context it was set in."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        require(
            "permission",
            "You must specify the permission!",
            description = "The permission node to unset.",
            type = ArgType.STRING,
            example = "myplugin.vip.access"
        )
        optional(
            "transient",
            description = "If the permission should be removed from transient data instead of normal data.",
            type = ArgType.BOOLEAN,
            default = "false"
        )
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val player: Player = data.player ?: return false
        val permission = config.getString("permission")

        return modifyUser(player) { user ->
            user.dataFor(config).clear { node ->
                NodeType.PERMISSION.matches(node) && node.key.equals(permission, ignoreCase = true)
            }
        }
    }
}
