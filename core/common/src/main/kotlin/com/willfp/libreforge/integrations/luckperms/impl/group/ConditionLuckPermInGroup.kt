package com.willfp.libreforge.integrations.luckperms.impl.group

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.get
import com.willfp.libreforge.getFormattedStrings
import com.willfp.libreforge.integrations.luckperms.LuckPermsIntegration.getLuckPermsUser
import net.luckperms.api.node.NodeType
import net.luckperms.api.node.types.InheritanceNode
import org.bukkit.entity.Player

object ConditionLuckPermInGroup : Condition<NoCompileData>("in_group") {
    override val arguments = arguments {
        require(listOf("group", "groups"), "You must specify the group(s)!")
    }

    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ): Boolean {
        val player = dispatcher.get<Player>() ?: return false
        val user = player.getLuckPermsUser() ?: return false

        val groups = config.getFormattedStrings("groups", "group")

        return user.getNodes(NodeType.INHERITANCE)
            .map(InheritanceNode::getGroupName)
            .containsAll(groups)
    }
}