package com.willfp.libreforge.integrations.luckperms.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.get
import com.willfp.libreforge.integrations.luckperms.lpPermissionData
import net.luckperms.api.util.Tristate
import org.bukkit.entity.Player

object ConditionLpPermissionTristate : Condition<NoCompileData>("lp_permission_tristate") {
    override val description =
        "Passes when the permission resolves to the specified tristate value for the player."
    override val categories = setOf("permission")
    override val additionalInfo = listOf(
        "Requires the LuckPerms plugin.",
        "Unlike has_permission, this can check for UNDEFINED, meaning the permission is neither set nor unset.",
        "Uses the active LuckPerms contexts of the player."
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
            "state",
            description = "The tristate value the permission must resolve to.",
            type = ArgType.STRING,
            default = "TRUE",
            enumClass = Tristate::class
        )
    }

    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ): Boolean {
        val player = dispatcher.get<Player>() ?: return false
        val permissionData = player.lpPermissionData ?: return false

        val state = config.getStringOrNull("state")?.let {
            runCatching { Tristate.valueOf(it.uppercase()) }.getOrNull()
        } ?: Tristate.TRUE

        return permissionData.checkPermission(config.getString("permission")) == state
    }
}
