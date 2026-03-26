package com.willfp.libreforge.integrations.luckperms.impl.perms

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.map.listMap
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.effects.Identifiers
import com.willfp.libreforge.get
import com.willfp.libreforge.integrations.luckperms.LuckPermsIntegration.getLuckPermsUser
import com.willfp.libreforge.integrations.luckperms.LuckPermsIntegration.saveChanges
import net.luckperms.api.context.ImmutableContextSet
import net.luckperms.api.node.types.PermissionNode
import org.bukkit.entity.Player
import java.util.UUID

object EffectLuckPermGivePermission : Effect<NoCompileData>("give_permission") {
    override val arguments = arguments {
        require("permission", "You must specify the permission!")
    }

    private val permissions = listMap<UUID, GivenPermission>()

    override fun onEnable(
        dispatcher: Dispatcher<*>,
        config: Config,
        identifiers: Identifiers,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ) {
        val player = dispatcher.get<Player>() ?: return

        // UUID Version 2 = NPC
        if (player.uniqueId.version() == 2) {
            return
        }

        val user = player.getLuckPermsUser() ?: return
        val permission = config.getFormattedString("permission")
        val value = config.getBoolOrNull("value")
        val context = config.getFormattedStrings("context")
        val contextSet = ImmutableContextSet.builder()

        for (string in context) {
            val split = string.split(":")
            if (split.size != 2) continue
            contextSet.add(split[0], split[1])
        }

        val node = PermissionNode.builder(permission)
            .value(value ?: true)
            .context(contextSet.build())
            .build()

        permissions[player.uniqueId].add(GivenPermission(node, player.uniqueId))
        user.data().add(node)
        user.saveChanges()
    }

    override fun onDisable(dispatcher: Dispatcher<*>, identifiers: Identifiers, holder: ProvidedHolder) {
        val player = dispatcher.get<Player>() ?: return

        // UUID Version 2 = NPC
        if (player.uniqueId.version() == 2) {
            return
        }

        val user = player.getLuckPermsUser() ?: return
        val permission = permissions[player.uniqueId]
            .firstOrNull { it.uuid == identifiers.uuid } ?: return

        permissions[player.uniqueId].remove(permission)

        // Remove the permission only if no other effect is giving it
        if (permissions[dispatcher.uuid].none { it.node == permission.node }) {
            user.data().remove(permission.node)
            user.saveChanges()
        }
    }

    private data class GivenPermission(
        val node: PermissionNode,
        val uuid: UUID
    )
}