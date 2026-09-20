package com.willfp.libreforge.integrations.luckperms.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.integrations.luckperms.modifyUser
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter

object EffectLpSetPrimaryGroup : Effect<NoCompileData>("lp_set_primary_group") {
    override val description = "Sets the primary LuckPerms group of the player."
    override val categories = setOf("permission")
    override val additionalInfo = listOf(
        "Requires the LuckPerms plugin.",
        "Saves asynchronously, so the change is not visible immediately.",
        "The player must already be a member of the group for this to have an effect."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        require(
            "group",
            "You must specify the group!",
            description = "The name of the LuckPerms group to set as primary.",
            type = ArgType.STRING,
            example = "vip"
        )
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val player = data.player ?: return false
        val group = config.getString("group")

        return modifyUser(player) { user ->
            user.setPrimaryGroup(group)
        }
    }
}
