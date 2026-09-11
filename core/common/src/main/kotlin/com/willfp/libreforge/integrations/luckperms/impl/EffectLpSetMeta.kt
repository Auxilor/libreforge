package com.willfp.libreforge.integrations.luckperms.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getFormattedString
import com.willfp.libreforge.integrations.luckperms.dataFor
import com.willfp.libreforge.integrations.luckperms.getContexts
import com.willfp.libreforge.integrations.luckperms.getNodeExpiry
import com.willfp.libreforge.integrations.luckperms.modifyUser
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import net.luckperms.api.node.NodeType
import net.luckperms.api.node.types.MetaNode

object EffectLpSetMeta : Effect<NoCompileData>("lp_set_meta") {
    override val description = "Sets a LuckPerms meta value on the player."
    override val categories = setOf("permission")
    override val additionalInfo = listOf(
        "Requires the LuckPerms plugin.",
        "Saves asynchronously, so the change is not visible immediately.",
        "Removes any existing values for the key first, so the player only ever has one value for it."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        require(
            "key",
            "You must specify the meta key!",
            description = "The LuckPerms meta key to set.",
            type = ArgType.STRING,
            example = "rank-color"
        )
        require(
            "value",
            "You must specify the meta value!",
            description = "The value to set, supporting placeholders.",
            type = ArgType.STRING,
            example = "gold"
        )
        optional(
            "duration",
            description = "How long the meta should last, in seconds. Permanent if not specified.",
            type = ArgType.EXPRESSION,
            example = "86400"
        )
        optional(
            "contexts",
            description = "The LuckPerms contexts to apply the meta in, as a list of key=value entries.",
            type = ArgType.STRING_LIST,
            default = "[]",
            example = listOf("server=survival")
        )
        optional(
            "transient",
            description = "If the meta should be set transiently, meaning it is lost on server restart.",
            type = ArgType.BOOLEAN,
            default = "false"
        )
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val player = data.player ?: return false
        val key = config.getString("key")
        val value = config.getFormattedString("value", data)
        val expiry = config.getNodeExpiry(data)
        val contexts = config.getContexts("contexts")

        return modifyUser(player) { user ->
            val nodeMap = user.dataFor(config)

            nodeMap.clear { node ->
                NodeType.META.tryCast(node).map { it.metaKey.equals(key, ignoreCase = true) }.orElse(false)
            }

            val builder = MetaNode.builder(key, value)

            if (expiry != null) {
                builder.expiry(expiry)
            }

            if (!contexts.isEmpty) {
                builder.context(contexts)
            }

            nodeMap.add(builder.build())
        }
    }
}
