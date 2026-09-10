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
import net.luckperms.api.node.types.SuffixNode

object EffectLpSetSuffix : Effect<NoCompileData>("lp_set_suffix") {
    override val description = "Gives the player a LuckPerms suffix."
    override val categories = setOf("permission", "chat")
    override val additionalInfo = listOf(
        "Requires the LuckPerms plugin.",
        "Saves asynchronously, so the change is not visible immediately.",
        "The highest priority suffix the player has is the one that is displayed."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        require(
            "suffix",
            "You must specify the suffix!",
            description = "The suffix to give, supporting placeholders.",
            type = ArgType.STRING,
            example = " &7(Veteran)"
        )
        optional(
            "priority",
            description = "The priority of the suffix.",
            type = ArgType.EXPRESSION,
            default = "100"
        )
        optional(
            "duration",
            description = "How long the suffix should last, in seconds. Permanent if not specified.",
            type = ArgType.EXPRESSION,
            example = "86400"
        )
        optional(
            "contexts",
            description = "The LuckPerms contexts to apply the suffix in, as a list of key=value entries.",
            type = ArgType.STRING_LIST,
            default = "[]",
            example = listOf("server=survival")
        )
        optional(
            "transient",
            description = "If the suffix should be set transiently, meaning it is lost on server restart.",
            type = ArgType.BOOLEAN,
            default = "false"
        )
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val player = data.player ?: return false
        val suffix = config.getFormattedString("suffix", data)
        val priority = if (config.has("priority")) config.getIntFromExpression("priority", data) else 100
        val expiry = config.getNodeExpiry(data)
        val contexts = config.getContexts("contexts")

        return modifyUser(player) { user ->
            val builder = SuffixNode.builder(suffix, priority)

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
