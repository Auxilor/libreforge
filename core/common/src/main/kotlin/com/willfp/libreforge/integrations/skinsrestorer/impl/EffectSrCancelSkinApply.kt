package com.willfp.libreforge.integrations.skinsrestorer.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.integrations.skinsrestorer.currentSkinApplyEvent
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter

object EffectSrCancelSkinApply : Effect<NoCompileData>("sr_cancel_skin_apply") {
    override val description = "Cancels the skin that SkinsRestorer is about to apply."

    override val categories = setOf("player", "meta")

    override val additionalInfo = listOf(
        "Requires SkinsRestorer to be installed.",
        "Only works on the sr_apply_skin trigger, and only without a delay. " +
            "SkinApplyEvent is not a bukkit event, so cancel_event cannot be used for it."
    )

    override val supportsDelay = false

    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val event = currentSkinApplyEvent.get() ?: return false

        event.isCancelled = true

        return true
    }
}
