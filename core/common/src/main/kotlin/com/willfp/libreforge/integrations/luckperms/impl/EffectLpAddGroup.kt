package com.willfp.libreforge.integrations.luckperms.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.integrations.luckperms.dataFor
import com.willfp.libreforge.integrations.luckperms.getContexts
import com.willfp.libreforge.integrations.luckperms.getNodeExpiry
import com.willfp.libreforge.integrations.luckperms.modifyUser
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import net.luckperms.api.node.types.InheritanceNode

object EffectLpAddGroup : Effect<NoCompileData>("lp_add_group") {
    override val description = "Adds the player to a LuckPerms group."
    override val categories = setOf("permission")
    override val additionalInfo = listOf(
        "Requires the LuckPerms plugin.",
        "Saves asynchronously, so the change is not visible immediately."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        require(
            "group",
            "You must specify the group!",
            description = "The name of the LuckPerms group to add.",
            type = ArgType.STRING,
            example = "vip"
        )
        optional(
            "duration",
            description = "How long the group should last, in seconds. Permanent if not specified.",
            type = ArgType.EXPRESSION,
            example = "86400"
        )
        optional(
            "contexts",
            description = "The LuckPerms contexts to apply the group in, as a list of key=value entries.",
            type = ArgType.STRING_LIST,
            default = "[]",
            example = listOf("server=survival", "world=world_nether")
        )
        optional(
            "transient",
            description = "If the group should be added transiently, meaning it is lost on server restart.",
            type = ArgType.BOOLEAN,
            default = "false"
        )
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val player = data.player ?: return false
        val group = config.getString("group")
        val expiry = config.getNodeExpiry(data)
        val contexts = config.getContexts("contexts")

        return modifyUser(player) { user ->
            val builder = InheritanceNode.builder(group)

            if (expiry != null) {
                builder.expiry(expiry)
            }

            if (!contexts.isEmpty) {
                builder.context(contexts)
            }

            user.dataFor(config).add(builder.build())
        }
    }
}
