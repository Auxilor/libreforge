package com.willfp.libreforge.integrations.luckperms.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.integrations.luckperms.LuckPermsManager
import com.willfp.libreforge.integrations.luckperms.lpUser
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter

object EffectLpPushUserUpdate : Effect<NoCompileData>("lp_push_user_update") {
    override val description = "Pushes an update for the player over the LuckPerms messaging service."
    override val categories = setOf("permission")
    override val additionalInfo = listOf(
        "Requires the LuckPerms plugin.",
        "Requires a LuckPerms messaging service to be configured, otherwise this does nothing.",
        "Tells other servers on the network to reload the data of the player."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val player = data.player ?: return false
        val user = player.lpUser ?: return false
        val messagingService = LuckPermsManager.luckPerms?.messagingService?.orElse(null) ?: return false

        messagingService.pushUserUpdate(user)

        return true
    }
}
