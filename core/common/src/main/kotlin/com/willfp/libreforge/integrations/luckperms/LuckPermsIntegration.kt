package com.willfp.libreforge.integrations.luckperms

import com.willfp.eco.core.EcoPlugin
import com.willfp.libreforge.conditions.Conditions
import com.willfp.libreforge.effects.Effects
import com.willfp.libreforge.integrations.LoadableIntegration
import com.willfp.libreforge.integrations.luckperms.impl.group.ConditionLuckPermInGroup
import com.willfp.libreforge.integrations.luckperms.impl.group.EffectLuckPermDisinheritGroup
import com.willfp.libreforge.integrations.luckperms.impl.group.EffectLuckPermInheritGroup
import com.willfp.libreforge.integrations.luckperms.impl.group.TriggerLuckPermsGroupChange
import com.willfp.libreforge.integrations.luckperms.impl.group.TriggerLuckPermsGroupDisinherit
import com.willfp.libreforge.integrations.luckperms.impl.group.TriggerLuckPermsGroupInherit
import com.willfp.libreforge.integrations.luckperms.impl.meta.ConditionLuckPermCheckMeta
import com.willfp.libreforge.integrations.luckperms.impl.meta.EffectLuckPermRemoveMeta
import com.willfp.libreforge.integrations.luckperms.impl.meta.EffectLuckPermSetMeta
import com.willfp.libreforge.integrations.luckperms.impl.meta.TriggerLuckPermsMetaAdd
import com.willfp.libreforge.integrations.luckperms.impl.meta.TriggerLuckPermsMetaChange
import com.willfp.libreforge.integrations.luckperms.impl.meta.TriggerLuckPermsMetaRemove
import com.willfp.libreforge.integrations.luckperms.impl.perms.ConditionLuckPermHasPermission
import com.willfp.libreforge.integrations.luckperms.impl.perms.EffectLuckPermGivePermission
import com.willfp.libreforge.integrations.luckperms.impl.perms.EffectLuckPermRemovePermission
import com.willfp.libreforge.integrations.luckperms.impl.perms.EffectLuckPermSetPermission
import com.willfp.libreforge.integrations.luckperms.impl.perms.TriggerLuckPermsPermissionAdd
import com.willfp.libreforge.integrations.luckperms.impl.perms.TriggerLuckPermsPermissionChange
import com.willfp.libreforge.integrations.luckperms.impl.perms.TriggerLuckPermsPermissionRemove
import com.willfp.libreforge.integrations.luckperms.impl.regex.EffectLuckPermGiveRegexPermission
import com.willfp.libreforge.integrations.luckperms.impl.regex.EffectLuckPermRemoveRegexPermission
import com.willfp.libreforge.integrations.luckperms.impl.regex.EffectLuckPermSetRegexPermission
import com.willfp.libreforge.integrations.luckperms.impl.regex.TriggerLuckPermsRegexPermissionAdd
import com.willfp.libreforge.integrations.luckperms.impl.regex.TriggerLuckPermsRegexPermissionChange
import com.willfp.libreforge.integrations.luckperms.impl.regex.TriggerLuckPermsRegexPermissionRemove
import com.willfp.libreforge.triggers.Triggers
import net.luckperms.api.LuckPermsProvider
import net.luckperms.api.model.user.User
import org.bukkit.entity.Player

object LuckPermsIntegration : LoadableIntegration {
    override fun load(plugin: EcoPlugin) {
        Conditions.register(ConditionLuckPermHasPermission)
        Conditions.register(ConditionLuckPermCheckMeta)
        Conditions.register(ConditionLuckPermInGroup)
        //
        Effects.register(EffectLuckPermGivePermission)
        Effects.register(EffectLuckPermSetPermission)
        Effects.register(EffectLuckPermRemovePermission)
        Effects.register(EffectLuckPermGiveRegexPermission)
        Effects.register(EffectLuckPermSetRegexPermission)
        Effects.register(EffectLuckPermRemoveRegexPermission)
        Effects.register(EffectLuckPermSetMeta)
        Effects.register(EffectLuckPermRemoveMeta)
        Effects.register(EffectLuckPermInheritGroup)
        Effects.register(EffectLuckPermDisinheritGroup)
        //
        Triggers.register(TriggerLuckPermsPermissionAdd)
        Triggers.register(TriggerLuckPermsPermissionChange)
        Triggers.register(TriggerLuckPermsPermissionRemove)
        Triggers.register(TriggerLuckPermsRegexPermissionAdd)
        Triggers.register(TriggerLuckPermsRegexPermissionChange)
        Triggers.register(TriggerLuckPermsRegexPermissionRemove)
        Triggers.register(TriggerLuckPermsMetaAdd)
        Triggers.register(TriggerLuckPermsMetaChange)
        Triggers.register(TriggerLuckPermsMetaRemove)
        Triggers.register(TriggerLuckPermsGroupInherit)
        Triggers.register(TriggerLuckPermsGroupDisinherit)
        Triggers.register(TriggerLuckPermsGroupChange)
    }

    override fun getPluginName(): String {
        return "LuckPerms"
    }

    fun Player.getLuckPermsUser(): User? {
        return LuckPermsProvider.get().userManager.getUser(uniqueId)
    }

    fun User.saveChanges() {
        LuckPermsProvider.get().userManager.saveUser(this)
    }
}
