package com.willfp.libreforge.integrations.luckperms

import com.willfp.eco.core.EcoPlugin
import com.willfp.libreforge.conditions.Conditions
import com.willfp.libreforge.effects.Effects
import com.willfp.libreforge.integrations.LoadableIntegration
import com.willfp.libreforge.integrations.luckperms.impl.ConditionLuckPermHasPermission
import com.willfp.libreforge.integrations.luckperms.impl.EffectLuckPermGivePermission
import net.luckperms.api.LuckPermsProvider
import net.luckperms.api.model.user.User
import org.bukkit.entity.Player

object LuckPermsIntegration : LoadableIntegration {
    override fun load(plugin: EcoPlugin) {
        Conditions.register(ConditionLuckPermHasPermission)
        Effects.register(EffectLuckPermGivePermission)
    }

    override fun getPluginName(): String {
        return "LuckPerms"
    }

    fun Player.getLuckPermsUser(): User? {
        return LuckPermsProvider.get().userManager.getUser(uniqueId)
    }
}
