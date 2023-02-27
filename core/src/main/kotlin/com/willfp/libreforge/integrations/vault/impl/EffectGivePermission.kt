package com.willfp.libreforge.integrations.vault.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.map.nestedMap
import com.willfp.libreforge.NoCompileData
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

    private val permissions = nestedMap<UUID, UUID, String>()

    override fun onEnable(player: Player, config: Config, identifiers: Identifiers, compileData: NoCompileData) {
        val permission = config.getString("permission")

        permissions[player.uniqueId][identifiers.uuid] += permission
        handler.playerAdd(player, permission)
    }

    override fun onDisable(player: Player, identifiers: Identifiers) {
        permissions[player.uniqueId][identifiers.uuid]?.let {
            handler.playerRemove(player, it)
        }
        permissions[player.uniqueId].remove(identifiers.uuid)
    }
}
