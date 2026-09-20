package com.willfp.libreforge.integrations.luckperms.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getFormattedString
import com.willfp.libreforge.integrations.luckperms.LuckPermsManager
import com.willfp.libreforge.triggers.TriggerData
import java.util.UUID

object EffectLpLogAction : Effect<NoCompileData>("lp_log_action") {
    override val description = "Submits an entry to the LuckPerms action log."
    override val categories = setOf("permission", "meta")
    override val additionalInfo = listOf(
        "Requires the LuckPerms plugin.",
        "The entry is attributed to the player if the trigger has one, and to the console otherwise."
    )

    override val isPermanent = false

    private val consoleUuid = UUID(0, 0)

    override val arguments = arguments {
        require(
            "description",
            "You must specify the description!",
            description = "The description of the action, supporting placeholders.",
            type = ArgType.STRING,
            example = "Won the event"
        )
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val actionLogger = LuckPermsManager.luckPerms?.actionLogger ?: return false
        val player = data.player

        val action = actionLogger.actionBuilder()
            .source(player?.uniqueId ?: consoleUuid)
            .sourceName(player?.name ?: "Console")
            .targetType(net.luckperms.api.actionlog.Action.Target.Type.USER)
            .target(player?.uniqueId ?: consoleUuid)
            .targetName(player?.name ?: "Console")
            .description(config.getFormattedString("description", data))
            .build()

        actionLogger.submit(action)

        return true
    }
}
