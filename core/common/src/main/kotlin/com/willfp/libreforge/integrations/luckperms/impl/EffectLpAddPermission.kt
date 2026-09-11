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
import net.luckperms.api.node.types.PermissionNode

object EffectLpAddPermission : Effect<NoCompileData>("lp_add_permission") {
    override val description = "Sets a permission on the player in LuckPerms."
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
            "permission",
            "You must specify the permission!",
            description = "The permission node to set.",
            type = ArgType.STRING,
            example = "myplugin.vip.access"
        )
        optional(
            "value",
            description = "The value of the permission. Set to false to explicitly deny it.",
            type = ArgType.BOOLEAN,
            default = "true"
        )
        optional(
            "duration",
            description = "How long the permission should last, in seconds. Permanent if not specified.",
            type = ArgType.EXPRESSION,
            example = "86400"
        )
        optional(
            "contexts",
            description = "The LuckPerms contexts to apply the permission in, as a list of key=value entries.",
            type = ArgType.STRING_LIST,
            default = "[]",
            example = listOf("server=survival")
        )
        optional(
            "transient",
            description = "If the permission should be added transiently, meaning it is lost on server restart.",
            type = ArgType.BOOLEAN,
            default = "false"
        )
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val player = data.player ?: return false
        val permission = config.getString("permission")
        val value = config.getBoolOrNull("value") ?: true
        val expiry = config.getNodeExpiry(data)
        val contexts = config.getContexts("contexts")

        return modifyUser(player) { user ->
            val builder = PermissionNode.builder(permission)
                .value(value)

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
