package com.willfp.libreforge.integrations.luckperms.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.arguments
import com.willfp.libreforge.integrations.luckperms.LuckPermsManager
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.mutators.Mutator
import com.willfp.libreforge.mutators.parameterTransformers
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter

object MutatorLpTextToGroupDisplayName : Mutator<NoCompileData>("lp_text_to_group_display_name") {
    override val description = "Sets the text to the display name of a LuckPerms group."

    override val categories = setOf("chat", "permission")

    override val additionalInfo = listOf(
        "Requires the LuckPerms plugin.",
        "No-ops if the group is not loaded."
    )

    override val arguments = arguments {
        require(
            "group",
            "You must specify the group!",
            description = "The name of the LuckPerms group to read the display name from.",
            type = ArgType.STRING,
            example = "vip"
        )
    }

    override val parameterTransformers = parameterTransformers {
        adds(TriggerParameter.TEXT)
    }

    override fun mutate(data: TriggerData, config: Config, compileData: NoCompileData): TriggerData {
        val group = LuckPermsManager.luckPerms?.groupManager?.getGroup(config.getString("group")) ?: return data

        return data.copy(
            text = group.displayName ?: group.name
        )
    }
}
