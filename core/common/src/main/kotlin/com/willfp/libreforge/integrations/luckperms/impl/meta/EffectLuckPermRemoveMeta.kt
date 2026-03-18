package com.willfp.libreforge.integrations.luckperms.impl.meta

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.integrations.luckperms.LuckPermsIntegration.getLuckPermsUser
import com.willfp.libreforge.integrations.luckperms.LuckPermsIntegration.saveChanges
import com.willfp.libreforge.triggers.TriggerData
import net.luckperms.api.context.ImmutableContextSet
import net.luckperms.api.node.types.MetaNode

object EffectLuckPermRemoveMeta : Effect<NoCompileData>("remove_meta") {
    override val arguments = arguments {
        require("key", "You must specify the key!")
        require("value", "You must specify the value!")
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val player = data.player ?: return false

        // UUID Version 2 = NPC
        if (player.uniqueId.version() == 2) {
            return false
        }

        val user = player.getLuckPermsUser() ?: return false
        val key = config.getFormattedString("key")
        val value = config.getFormattedString("value")
        val context = config.getFormattedStrings("context")
        val contextSet = ImmutableContextSet.builder()

        for (string in context) {
            val split = string.split(":")
            if (split.size != 2) continue
            contextSet.add(split[0], split[1])
        }

        val node = MetaNode.builder(key, value)
            .context(contextSet.build())
            .build()

        user.data().remove(node)
        user.saveChanges()
        return true
    }
}