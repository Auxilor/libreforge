package com.willfp.libreforge.integrations.luckperms.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.integrations.luckperms.lpQueryOptions
import com.willfp.libreforge.integrations.luckperms.lpUser
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.mutators.Mutator
import com.willfp.libreforge.mutators.parameterTransformers
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter

object MutatorLpValueToGroupCount : Mutator<NoCompileData>("lp_value_to_group_count") {
    override val description = "Sets the value to the number of LuckPerms groups the player is in."

    override val categories = setOf("value", "permission")

    override val additionalInfo = listOf(
        "Requires the LuckPerms plugin.",
        "Counts inherited groups.",
        "No-ops if the LuckPerms data of the player is not loaded."
    )

    override val parameterTransformers = parameterTransformers {
        adds(TriggerParameter.VALUE)
    }

    override fun mutate(data: TriggerData, config: Config, compileData: NoCompileData): TriggerData {
        val player = data.player ?: return data
        val user = player.lpUser ?: return data

        return data.copy(
            value = user.getInheritedGroups(player.lpQueryOptions).size.toDouble()
        )
    }
}
