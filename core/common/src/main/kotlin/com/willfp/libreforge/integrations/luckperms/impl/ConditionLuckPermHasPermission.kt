package com.willfp.libreforge.integrations.luckperms.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.get
import com.willfp.libreforge.integrations.luckperms.LuckPermsIntegration.getLuckPermsUser
import org.bukkit.entity.Player

object ConditionLuckPermHasPermission : Condition<NoCompileData>("has_permission") {
    override val arguments = arguments {
        require("permission", "You must specify the permission!")
    }

    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ): Boolean {
        val player = dispatcher.get<Player>() ?: return false

        val user = player.getLuckPermsUser() ?: return false

        return user.cachedData.permissionData.checkPermission(config.getString("permission")).asBoolean()
    }
}