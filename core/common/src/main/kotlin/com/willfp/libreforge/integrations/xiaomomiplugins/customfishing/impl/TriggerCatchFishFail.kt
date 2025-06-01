package com.willfp.libreforge.integrations.xiaomomiplugins.customfishing.impl

import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import net.momirealms.customfishing.api.event.FishingResultEvent
import org.bukkit.event.EventHandler

object TriggerCatchFishFail : Trigger("catch_fish_fail") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.EVENT,
        TriggerParameter.TEXT
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: FishingResultEvent) {
        val player = event.player ?: return
        val result = event.result ?: return

        if (result != FishingResultEvent.Result.FAILURE) return

        this.dispatch(
            player.toDispatcher(),
            TriggerData(
                player = player,
                event = event,
                text = result.name
            )
        )
    }
}
