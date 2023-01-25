package com.willfp.libreforge.integrations.vault

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.effects.Identifiers
import net.milkbowl.vault.permission.Permission
import org.bukkit.entity.Player
import java.util.UUID

class EffectGivePermission(
    private val handler: Permission
) : Effect("give_permission") {
    override val arguments = arguments {
        require("permission", "You must specify the permission!")
    }

    private val permissions = mutableMapOf<UUID, MutableMap<UUID, String>>()

    override fun handleEnable(
        player: Player,
        config: Config,
        identifiers: Identifiers
    ) {
        val activePermissions = permissions[player.uniqueId] ?: mutableMapOf()
        val uuid = identifiers.uuid
        val perm = config.getString("permission")

        handler.playerAdd(player, perm)

        activePermissions[uuid] = perm
        permissions[player.uniqueId] = activePermissions
    }

    override fun handleDisable(
        player: Player,
        identifiers: Identifiers
    ) {
        val activePermissions = permissions[player.uniqueId] ?: mutableMapOf()
        val uuid = identifiers.uuid
        activePermissions[uuid]?.let {
            handler.playerRemove(player, it)
        }
        activePermissions.remove(uuid)
        permissions[player.uniqueId] = activePermissions
    }
}
