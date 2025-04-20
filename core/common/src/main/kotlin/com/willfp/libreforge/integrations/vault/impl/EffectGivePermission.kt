package com.willfp.libreforge.integrations.vault.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.map.listMap
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.effects.Identifiers
import com.willfp.libreforge.get
import net.milkbowl.vault.permission.Permission
import org.bukkit.entity.Player
import java.util.UUID

class EffectGivePermission(
    private val handler: Permission
) : Effect<NoCompileData>("give_permission") {
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

        val permission = config.getString("permission")

        permissions[dispatcher.uuid].add(GivenPermission(permission, identifiers.uuid))
        handler.playerAdd(player, permission)
    }

    override fun onDisable(dispatcher: Dispatcher<*>, identifiers: Identifiers, holder: ProvidedHolder) {
        val player = dispatcher.get<Player>() ?: return

        val permission = permissions[dispatcher.uuid]
            .firstOrNull { it.uuid == identifiers.uuid } ?: return

        permissions[dispatcher.uuid].remove(permission)

        // Remove the permission only if no other effect is giving it
        if (permissions[dispatcher.uuid].none { it.permission == permission.permission }) {
            // UUID Version 2 = NPC
            if (player.uniqueId.version() == 2) {
                return
            }

            handler.playerRemove(player, permission.permission)
        }
    }

    private data class GivenPermission(
        val permission: String,
        val uuid: UUID
    )
}
