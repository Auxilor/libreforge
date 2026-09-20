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
import net.luckperms.api.node.Node
import net.luckperms.api.node.NodeType

object EffectLpClearNodes : Effect<NoCompileData>("lp_clear_nodes") {
    override val description = "Clears LuckPerms nodes from the player."
    override val categories = setOf("permission")
    override val additionalInfo = listOf(
        "Requires the LuckPerms plugin.",
        "Saves asynchronously, so the change is not visible immediately.",
        "Clears every node the player has set directly if no type is specified."
    )

    private val types = listOf(
        "PERMISSION",
        "REGEX_PERMISSION",
        "INHERITANCE",
        "PREFIX",
        "SUFFIX",
        "META",
        "WEIGHT",
        "DISPLAY_NAME"
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        optional(
            "type",
            description = "The type of node to clear. Clears every node if not specified.",
            type = ArgType.STRING,
            choices = types
        )
        optional(
            "transient",
            description = "If transient data should be cleared instead of normal data.",
            type = ArgType.BOOLEAN,
            default = "false"
        )
    }

    private fun nodeTypeOf(name: String): NodeType<out Node>? = when (name.uppercase()) {
        "PERMISSION" -> NodeType.PERMISSION
        "REGEX_PERMISSION" -> NodeType.REGEX_PERMISSION
        "INHERITANCE" -> NodeType.INHERITANCE
        "PREFIX" -> NodeType.PREFIX
        "SUFFIX" -> NodeType.SUFFIX
        "META" -> NodeType.META
        "WEIGHT" -> NodeType.WEIGHT
        "DISPLAY_NAME" -> NodeType.DISPLAY_NAME
        else -> null
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val player = data.player ?: return false
        val typeName = config.getStringOrNull("type")

        val type = if (typeName == null) null else nodeTypeOf(typeName) ?: return false

        return modifyUser(player) { user ->
            val nodeMap = user.dataFor(config)

            if (type == null) {
                nodeMap.clear()
            } else {
                nodeMap.clear { node -> type.matches(node) }
            }
        }
    }
}
