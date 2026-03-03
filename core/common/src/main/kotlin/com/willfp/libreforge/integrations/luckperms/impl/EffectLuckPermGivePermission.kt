package com.willfp.libreforge.integrations.luckperms.impl

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
        val user = player.getLuckPermsUser() ?: return
        val permission = config.getString("permission")

        val node = PermissionNode.builder(permission).value(true).build()

        permissions[player.uniqueId].add(GivenPermission(node, player.uniqueId))
        user.data().add(node)
    }

    override fun onDisable(dispatcher: Dispatcher<*>, identifiers: Identifiers, holder: ProvidedHolder) {
        val player = dispatcher.get<Player>() ?: return
        val user = player.getLuckPermsUser() ?: return

        val givenPermission = permissions[player.uniqueId]
            .firstOrNull { it.uuid == identifiers.uuid } ?: return

        permissions[player.uniqueId].remove(givenPermission)
        user.data().remove(givenPermission.node)
    }

    private data class GivenPermission(
        val node: PermissionNode,
        val uuid: UUID
    )
}