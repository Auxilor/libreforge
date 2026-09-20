package com.willfp.libreforge.integrations.luckperms.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getFormattedString
import com.willfp.libreforge.getIntFromExpression
import com.willfp.libreforge.integrations.luckperms.dataFor
import com.willfp.libreforge.integrations.luckperms.getContexts
import com.willfp.libreforge.integrations.luckperms.getNodeExpiry
import com.willfp.libreforge.integrations.luckperms.modifyUser
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import net.luckperms.api.node.types.PrefixNode

object EffectLpSetPrefix : Effect<NoCompileData>("lp_set_prefix") {
    override val description = "Gives the player a LuckPerms prefix."
    override val categories = setOf("permission", "chat")
    override val additionalInfo = listOf(
        "Requires the LuckPerms plugin.",
        "Saves asynchronously, so the change is not visible immediately.",
        "The highest priority prefix the player has is the one that is displayed."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        require(
            "prefix",
            "You must specify the prefix!",
            description = "The prefix to give, supporting placeholders.",
            type = ArgType.STRING,
            example = "&6[VIP] "
        )
        optional(
            "priority",
            description = "The priority of the prefix.",
            type = ArgType.EXPRESSION,
            default = "100"
        )
        optional(
            "duration",
            description = "How long the prefix should last, in seconds. Permanent if not specified.",
            type = ArgType.EXPRESSION,
            example = "86400"
        )
        optional(
            "contexts",
            description = "The LuckPerms contexts to apply the prefix in, as a list of key=value entries.",
            type = ArgType.STRING_LIST,
            default = "[]",
            example = listOf("server=survival")
        )
        optional(
            "transient",
            description = "If the prefix should be set transiently, meaning it is lost on server restart.",
            type = ArgType.BOOLEAN,
            default = "false"
        )
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val player = data.player ?: return false
        val prefix = config.getFormattedString("prefix", data)
        val priority = if (config.has("priority")) config.getIntFromExpression("priority", data) else 100
        val expiry = config.getNodeExpiry(data)
        val contexts = config.getContexts("contexts")

        return modifyUser(player) { user ->
            val builder = PrefixNode.builder(prefix, priority)

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
