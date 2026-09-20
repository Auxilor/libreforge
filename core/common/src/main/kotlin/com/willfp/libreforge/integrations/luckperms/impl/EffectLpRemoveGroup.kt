package com.willfp.libreforge.integrations.luckperms.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.integrations.luckperms.dataFor
import com.willfp.libreforge.integrations.luckperms.getContexts
import com.willfp.libreforge.integrations.luckperms.modifyUser
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import net.luckperms.api.node.types.InheritanceNode

object EffectLpRemoveGroup : Effect<NoCompileData>("lp_remove_group") {
    override val description = "Removes the player from a LuckPerms group."
    override val categories = setOf("permission")
    override val additionalInfo = listOf(
        "Requires the LuckPerms plugin.",
        "Saves asynchronously, so the change is not visible immediately.",
        "Only removes the group in the specified contexts, so a group added in other contexts is kept."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        require(
            "group",
            "You must specify the group!",
            description = "The name of the LuckPerms group to remove.",
            type = ArgType.STRING,
            example = "vip"
        )
        optional(
            "contexts",
            description = "The LuckPerms contexts to remove the group from, as a list of key=value entries.",
            type = ArgType.STRING_LIST,
            default = "[]",
            example = listOf("server=survival")
        )
        optional(
            "transient",
            description = "If the group should be removed from transient data instead of normal data.",
            type = ArgType.BOOLEAN,
            default = "false"
        )
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val player = data.player ?: return false
        val group = config.getString("group")
        val contexts = config.getContexts("contexts")

        return modifyUser(player) { user ->
            val builder = InheritanceNode.builder(group)

            if (!contexts.isEmpty) {
                builder.context(contexts)
            }

            user.dataFor(config).remove(builder.build())
        }
    }
}
