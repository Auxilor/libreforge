package com.willfp.libreforge.integrations.luckperms.impl.perms

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.integrations.luckperms.LuckPermsIntegration.getLuckPermsUser
import com.willfp.libreforge.integrations.luckperms.LuckPermsIntegration.saveChanges
import com.willfp.libreforge.triggers.TriggerData
import net.luckperms.api.context.ImmutableContextSet
import net.luckperms.api.node.types.PermissionNode

object EffectLuckPermRemovePermission : Effect<NoCompileData>("remove_permission") {
    override val arguments = arguments {
        require("permission", "You must specify the permission!")
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val player = data.player ?: return false

        // UUID Version 2 = NPC
        if (player.uniqueId.version() == 2) {
            return false
        }

        val user = player.getLuckPermsUser() ?: return false
        val permission = config.getFormattedString("permission")
        val context = config.getFormattedStrings("context")
        val contextSet = ImmutableContextSet.builder()

        for (string in context) {
            val split = string.split(":")
            if (split.size != 2) continue
            contextSet.add(split[0], split[1])
        }

        val node = PermissionNode.builder(permission)
            .value(true)
            .context(contextSet.build())
            .build()

        user.data().remove(node)
        user.saveChanges()
        return true
    }
}