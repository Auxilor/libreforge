package com.willfp.libreforge.integrations.luckperms.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.get
import com.willfp.libreforge.integrations.luckperms.getContexts
import com.willfp.libreforge.integrations.luckperms.lpQueryOptions
import com.willfp.libreforge.integrations.luckperms.lpUser
import org.bukkit.entity.Player

object ConditionLpHasPermission : Condition<NoCompileData>("lp_has_permission") {
    override val description = "Passes when the player has the specified permission node in LuckPerms."
    override val categories = setOf("permission")
    override val additionalInfo = listOf(
        "Requires the LuckPerms plugin.",
        "Unlike has_permission, this can check the permission in specific LuckPerms contexts.",
        "Uses the active LuckPerms contexts of the player if no contexts are specified."
    )

    override val arguments = arguments {
        require(
            "permission",
            "You must specify the permission!",
            description = "The permission node to check.",
            type = ArgType.STRING,
            example = "myplugin.vip.access"
        )
        optional(
            "contexts",
            description = "The LuckPerms contexts to check the permission in, as a list of key=value entries.",
            type = ArgType.STRING_LIST,
            default = "[]",
            example = listOf("server=survival")
        )
    }

    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ): Boolean {
        val player = dispatcher.get<Player>() ?: return false
        val user = player.lpUser ?: return false
        val contexts = config.getContexts("contexts")

        val queryOptions = if (contexts.isEmpty) {
            player.lpQueryOptions
        } else {
            player.lpQueryOptions.toBuilder().context(contexts).build()
        }

        return user.cachedData.getPermissionData(queryOptions)
            .checkPermission(config.getString("permission"))
            .asBoolean()
    }
}
