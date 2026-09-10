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

object ConditionLpHasMeta : Condition<NoCompileData>("lp_has_meta") {
    override val description = "Passes when the specified LuckPerms meta key is set for the player."
    override val categories = setOf("permission")
    override val additionalInfo = listOf("Requires the LuckPerms plugin.")

    override val arguments = arguments {
        require(
            "key",
            "You must specify the meta key!",
            description = "The LuckPerms meta key to look for.",
            type = ArgType.STRING,
            example = "rank-color"
        )
    }

    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ): Boolean {
        val player = dispatcher.get<Player>() ?: return false
        val metaData = player.lpMetaData ?: return false

        return metaData.getMetaValue(config.getString("key")) != null
    }
}
