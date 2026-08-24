package com.willfp.libreforge.integrations.vault

import com.willfp.eco.core.EcoPlugin
import com.willfp.libreforge.effects.Effects
import com.willfp.libreforge.integrations.LoadableIntegration
import com.willfp.libreforge.integrations.vault.impl.EffectGivePermission
import com.willfp.libreforge.integrations.vault.impl.VaultBalancePoller
import net.milkbowl.vault.economy.Economy
import net.milkbowl.vault.permission.Permission
import org.bukkit.Bukkit

object VaultIntegration : LoadableIntegration {
    override fun load(plugin: EcoPlugin) {
        val perms = Bukkit.getServer().servicesManager
            .getRegistration(Permission::class.java)?.provider

        if (perms != null) {
            Effects.register(EffectGivePermission(perms))
        }

        if (Bukkit.getServer().servicesManager.getRegistration(Economy::class.java) != null) {
            plugin.eventManager.registerListener(VaultBalancePoller)
            VaultBalancePoller.start()

            plugin.onDisable {
                VaultBalancePoller.stop()
            }
        }
    }

    override fun getPluginName(): String {
        return "Vault"
    }
}
