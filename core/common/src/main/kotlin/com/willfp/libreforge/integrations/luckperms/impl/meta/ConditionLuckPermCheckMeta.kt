package com.willfp.libreforge.integrations.luckperms.impl.meta

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.get
import com.willfp.libreforge.integrations.luckperms.LuckPermsIntegration.getLuckPermsUser
import org.bukkit.entity.Player

object ConditionLuckPermCheckMeta : Condition<NoCompileData>("check_meta") {
    override val arguments = arguments {
        require("key", "You must specify the key!")
        require("value", "You must specify the value!")
    }

    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ): Boolean {
        val player = dispatcher.get<Player>() ?: return false
        val user = player.getLuckPermsUser() ?: return false

        return user.cachedData.metaData
            .getMetaValue(config.getFormattedString("key"))
            .equals(config.getFormattedString("value"), ignoreCase = true)
    }
}