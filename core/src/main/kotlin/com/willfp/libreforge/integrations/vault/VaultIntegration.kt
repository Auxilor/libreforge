package com.willfp.libreforge.integrations.vault

import com.willfp.eco.core.integrations.Integration
import com.willfp.libreforge.effects.Effects
import com.willfp.libreforge.integrations.vault.impl.EffectGivePermission
import net.milkbowl.vault.permission.Permission
import org.bukkit.Bukkit

object VaultIntegration : Integration {
    fun load() {
        val perms = Bukkit.getServer().servicesManager
            .getRegistration(Permission::class.java)?.provider

        if (perms != null) {
            Effects.register(EffectGivePermission(perms))
        }
    }

    override fun getPluginName(): String {
        return "Vault"
    }
}
