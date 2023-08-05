package com.willfp.libreforge.integrations.vault.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.map.listMap
import com.willfp.eco.core.map.nestedMap
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.effects.Identifiers
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

    override fun onEnable(player: Player, config: Config, identifiers: Identifiers, holder: ProvidedHolder, compileData: NoCompileData) {
        val permission = config.getString("permission")

        permissions[player.uniqueId] += GivenPermission(permission, identifiers.uuid)
        handler.playerAdd(player, permission)
    }

    override fun onDisable(player: Player, identifiers: Identifiers, holder: ProvidedHolder) {
        val permission = permissions[player.uniqueId]
            .firstOrNull { it.uuid == identifiers.uuid } ?: return

        permissions[player.uniqueId].remove(permission)

        // Remove the permission only if no other effect is giving it
        if (permissions[player.uniqueId].none { it.permission == permission.permission }) {
            handler.playerRemove(player, permission.permission)
        }
    }

    private data class GivenPermission(
        val permission: String,
        val uuid: UUID
    )
}
