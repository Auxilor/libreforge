package com.willfp.libreforge.integrations.luckperms.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.integrations.luckperms.asLuckPermsEvent
import com.willfp.libreforge.integrations.luckperms.mutatedNode
import com.willfp.libreforge.triggers.TriggerData
import net.luckperms.api.event.LuckPermsEvent

object FilterLpIsNegated : Filter<NoCompileData, Boolean>("lp_is_negated") {
    override val description = "Matches when the LuckPerms node in the trigger is negated."
    override val categories = setOf("permission")
    override val valueType = ArgType.BOOLEAN
    override val additionalInfo = listOf(
        "Requires the LuckPerms plugin.",
        "A negated node is one set to false, explicitly denying a permission.",
        "Fails when the trigger has no LuckPerms node attached to it."
    )

    override fun getValue(config: Config, data: TriggerData?, key: String): Boolean {
        return config.getBool(key)
    }

    override fun isMet(data: TriggerData, value: Boolean, compileData: NoCompileData): Boolean {
        val event = data.event.asLuckPermsEvent<LuckPermsEvent>() ?: return false
        val node = event.mutatedNode ?: return false

        return node.isNegated == value
    }
}
