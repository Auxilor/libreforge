package com.willfp.libreforge.integrations.skinsrestorer.impl

import com.willfp.libreforge.integrations.skinsrestorer.currentSkinApplyEvent
import com.willfp.libreforge.integrations.skinsrestorer.invalidateSkinCache
import com.willfp.libreforge.integrations.skinsrestorer.skinsRestorer
import com.willfp.libreforge.integrations.skinsrestorer.textureHash
import com.willfp.libreforge.plugin
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import net.skinsrestorer.api.event.SkinApplyEvent
import org.bukkit.entity.Player

object TriggerSrApplySkin : Trigger("sr_apply_skin") {
    override val description = "Fires when SkinsRestorer applies a skin to a player."

    override val categories = setOf("player")

    override val additionalInfo = listOf(
        "Requires SkinsRestorer to be installed.",
        "SkinsRestorer applies skins off the main thread, so this trigger can fire asynchronously. " +
            "Effects that touch the world should be delayed so they run on the main thread.",
        "cancel_event does not work here, because SkinApplyEvent is not a bukkit event. " +
            "Use sr_cancel_skin_apply instead."
    )

    override val parameterDescriptions = mapOf(
        TriggerParameter.TEXT to "The texture hash of the skin being applied."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.TEXT
    )

    private var isSubscribed = false

    override fun postRegister() {
        if (isSubscribed) {
            return
        }

        val api = skinsRestorer ?: return

        isSubscribed = true

        api.eventBus.subscribe(plugin, SkinApplyEvent::class.java) { handle(it) }
    }

    private fun handle(event: SkinApplyEvent) {
        val player = runCatching { event.getPlayer(Player::class.java) }.getOrNull() ?: return

        invalidateSkinCache(player.uniqueId)

        /*

        The dispatch has to happen synchronously on the thread firing the event,
        otherwise sr_cancel_skin_apply and sr_set_applied_skin would run after
        SkinsRestorer has already acted on the event.

         */

        currentSkinApplyEvent.set(event)

        try {
            this.dispatch(
                player.toDispatcher(),
                TriggerData(
                    player = player,
                    text = event.property.textureHash()
                )
            )
        } finally {
            currentSkinApplyEvent.remove()
        }
    }
}
