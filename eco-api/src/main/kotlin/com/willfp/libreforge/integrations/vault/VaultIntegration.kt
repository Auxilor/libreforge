package com.willfp.libreforge.integrations.vault

import com.willfp.libreforge.effects.Effect
import net.milkbowl.vault.permission.Permission
import org.bukkit.Bukkit

object VaultIntegration {
    private lateinit var GIVE_PERMISSION: Effect

    fun load() {
        val perms = Bukkit.getServer().servicesManager
            .getRegistration(Permission::class.java)?.provider
        if (perms != null) {
            GIVE_PERMISSION = EffectGivePermission(perms)
        }
    }
}
