package com.willfp.libreforge.integrations.luckperms.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.get
import com.willfp.libreforge.integrations.luckperms.lpMetaData
import org.bukkit.entity.Player

object ConditionLpSuffixIs : Condition<NoCompileData>("lp_suffix_is") {
    override val description = "Passes when the active LuckPerms suffix of the player matches."
    override val categories = setOf("permission", "chat")
    override val additionalInfo = listOf(
        "Requires the LuckPerms plugin.",
        "Compares against the highest priority suffix, after meta stacking."
    )

    override val arguments = arguments {
        require(
            "suffix",
            "You must specify the suffix!",
            description = "The suffix the player must have.",
            type = ArgType.STRING,
            example = " &7(Veteran)"
        )
    }

    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ): Boolean {
        val player = dispatcher.get<Player>() ?: return false
        val suffix = player.lpMetaData?.suffix ?: return false

        return suffix == config.getString("suffix")
    }
}
