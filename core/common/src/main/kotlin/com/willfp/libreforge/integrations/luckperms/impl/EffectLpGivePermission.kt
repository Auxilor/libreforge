package com.willfp.libreforge.integrations.luckperms.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.map.listMap
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.effects.Identifiers
import com.willfp.libreforge.get
import com.willfp.libreforge.integrations.luckperms.getContexts
import com.willfp.libreforge.integrations.luckperms.modifyUser
import net.luckperms.api.model.user.User
import net.luckperms.api.node.types.PermissionNode
import org.bukkit.entity.Player
import java.util.UUID

object EffectLpGivePermission : Effect<NoCompileData>("lp_give_permission") {
    override val description =
        "Grants a LuckPerms permission node to the player while the holder is active, removing it when the holder is disabled."
    override val categories = setOf("permission")
    override val additionalInfo = listOf(
        "Requires the LuckPerms plugin.",
        "Unlike give_permission, this supports LuckPerms contexts, so the permission can be limited to one server on a network.",
        "Saves asynchronously, so the change is not visible immediately."
    )

    override val arguments = arguments {
        require(
            "permission",
            "You must specify the permission!",
            description = "The permission node to grant.",
            type = ArgType.STRING,
            example = "myplugin.vip.access"
        )
        optional(
            "value",
            description = "The value of the permission. Set to false to explicitly deny it.",
            type = ArgType.BOOLEAN,
            default = "true"
        )
        optional(
            "contexts",
            description = "The LuckPerms contexts to apply the permission in, as a list of key=value entries.",
            type = ArgType.STRING_LIST,
            default = "[]",
            example = listOf("server=survival")
        )
        optional(
            "transient",
            description = "If the permission should be added transiently, meaning it is never saved to storage.",
            type = ArgType.BOOLEAN,
            default = "true"
        )
    }

    private val permissions = listMap<UUID, GivenPermission>()

    override fun onEnable(
        dispatcher: Dispatcher<*>,
        config: Config,
        identifiers: Identifiers,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ) {
        val player = dispatcher.get<Player>() ?: return

        if (player.uniqueId.version() == 2) {
            return
        }

        val permission = GivenPermission(
            PermissionNode.builder(config.getString("permission"))
                .value(config.getBoolOrNull("value") ?: true)
                .context(config.getContexts("contexts"))
                .build(),
            config.getBoolOrNull("transient") ?: true,
            identifiers.uuid
        )

        permissions[dispatcher.uuid].add(permission)

        modifyUser(player) { user ->
            user.dataFor(permission).add(permission.node)
        }
    }

    override fun onDisable(dispatcher: Dispatcher<*>, identifiers: Identifiers, holder: ProvidedHolder) {
        val player = dispatcher.get<Player>() ?: return

        val permission = permissions[dispatcher.uuid]
            .firstOrNull { it.uuid == identifiers.uuid } ?: return

        permissions[dispatcher.uuid].remove(permission)

        if (permissions[dispatcher.uuid].none { it.node == permission.node && it.transient == permission.transient }) {
            modifyUser(player) { user ->
                user.dataFor(permission).remove(permission.node)
            }
        }
    }

    private fun User.dataFor(permission: GivenPermission) =
        if (permission.transient) this.transientData() else this.data()

    private data class GivenPermission(
        val node: PermissionNode,
        val transient: Boolean,
        val uuid: UUID
    )
}
