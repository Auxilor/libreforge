package com.willfp.libreforge.integrations.vault

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.effects.getEffectAmount
import net.milkbowl.vault.permission.Permission
import org.bukkit.entity.Player
import java.util.UUID

class EffectGivePermission(
    private val handler: Permission
) : Effect("give_permission") {
    private val permissions = mutableMapOf<UUID, MutableMap<UUID, String>>()

    override fun handleEnable(player: Player, config: Config) {
        val activePermissions = permissions[player.uniqueId] ?: mutableMapOf()
        val uuid = this.getUUID(player.getEffectAmount(this))
        val perm = config.getString("permission")

        handler.playerAdd(player, perm)

        activePermissions[uuid] = perm
        permissions[player.uniqueId] = activePermissions
    }

    override fun handleDisable(player: Player) {
        val activePermissions = permissions[player.uniqueId] ?: mutableMapOf()
        val uuid = this.getUUID(player.getEffectAmount(this))
        activePermissions[uuid]?.let {
            handler.playerRemove(player, it)
        }
        activePermissions.remove(uuid)
        permissions[player.uniqueId] = activePermissions
    }

    override fun validateConfig(config: Config): List<ConfigViolation> {
        val violations = mutableListOf<ConfigViolation>()

        if (!config.has("permission")) violations.add(
            ConfigViolation(
                "permission",
                "You must specify the permission!"
            )
        )

        return violations
    }
}
