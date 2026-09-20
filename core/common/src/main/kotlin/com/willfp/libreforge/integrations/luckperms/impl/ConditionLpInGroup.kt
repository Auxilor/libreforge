package com.willfp.libreforge.integrations.luckperms.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.get
import com.willfp.libreforge.integrations.luckperms.lpQueryOptions
import com.willfp.libreforge.integrations.luckperms.lpUser
import net.luckperms.api.model.user.User
import net.luckperms.api.node.NodeType
import org.bukkit.entity.Player

object ConditionLpInGroup : Condition<NoCompileData>("lp_in_group") {
    override val description = "Passes when the player is in the specified LuckPerms group."
    override val categories = setOf("permission")
    override val additionalInfo = listOf(
        "Requires the LuckPerms plugin.",
        "By default this checks inherited groups, so a player in a group that inherits the specified group passes.",
        "Set match to direct to only check groups the player is a member of, ignoring inheritance.",
        "Set match to primary to only check the primary group of the player."
    )

    private val matches = listOf("inherited", "direct", "primary")

    override val arguments = arguments {
        require(
            "group",
            "You must specify the group!",
            description = "The name of the LuckPerms group.",
            type = ArgType.STRING,
            example = "vip"
        )
        optional(
            "match",
            description = "How to match the group against the groups of the player.",
            type = ArgType.STRING,
            default = "inherited",
            choices = matches
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
        val group = config.getString("group")

        return when (config.getStringOrNull("match")?.lowercase()) {
            "direct" -> user.hasDirectGroup(group)
            "primary" -> user.primaryGroup.equals(group, ignoreCase = true)
            else -> user.hasInheritedGroup(player, group)
        }
    }

    private fun User.hasDirectGroup(group: String) =
        this.getNodes(NodeType.INHERITANCE).any { it.groupName.equals(group, ignoreCase = true) }

    private fun User.hasInheritedGroup(player: Player, group: String) =
        this.getInheritedGroups(player.lpQueryOptions).any { it.name.equals(group, ignoreCase = true) }
}
