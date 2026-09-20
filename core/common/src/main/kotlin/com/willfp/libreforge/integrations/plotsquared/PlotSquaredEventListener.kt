package com.willfp.libreforge.integrations.plotsquared

import com.google.common.eventbus.Subscribe
import com.plotsquared.core.events.PlayerEnterPlotEvent
import com.plotsquared.core.events.PlayerLeavePlotEvent
import com.plotsquared.core.events.PlotClaimedNotifyEvent
import com.plotsquared.core.events.PlotEvent
import com.plotsquared.core.events.post.PostPlayerAutoPlotEvent
import com.plotsquared.core.events.post.PostPlayerBuyPlotEvent
import com.plotsquared.core.events.post.PostPlayerPlotAddRemoveEvent
import com.plotsquared.core.events.post.PostPlayerPlotAddedEvent
import com.plotsquared.core.events.post.PostPlayerPlotTrustedEvent
import com.plotsquared.core.events.post.PostPlotClearEvent
import com.plotsquared.core.events.post.PostPlotMergeEvent
import com.plotsquared.core.events.post.PostPlotUnlinkEvent
import com.plotsquared.core.player.PlotPlayer
import com.willfp.libreforge.integrations.plotsquared.impl.TriggerPsAddedToPlot
import com.willfp.libreforge.integrations.plotsquared.impl.TriggerPsBuyPlot
import com.willfp.libreforge.integrations.plotsquared.impl.TriggerPsClaimPlot
import com.willfp.libreforge.integrations.plotsquared.impl.TriggerPsClearPlot
import com.willfp.libreforge.integrations.plotsquared.impl.TriggerPsEnterPlot
import com.willfp.libreforge.integrations.plotsquared.impl.TriggerPsLeavePlot
import com.willfp.libreforge.integrations.plotsquared.impl.TriggerPsMergePlots
import com.willfp.libreforge.integrations.plotsquared.impl.TriggerPsTrustedOnPlot
import com.willfp.libreforge.integrations.plotsquared.impl.TriggerPsUnlinkPlots
import com.willfp.libreforge.plugin
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.UUID

/**
 * PlotSquared events are dispatched through its own event bus rather than through Bukkit,
 * so they cannot be listened to with @EventHandler on the triggers themselves. Instead,
 * everything is subscribed to here and fanned out to the triggers.
 *
 * Some PlotSquared events are fired off the main thread, so every dispatch is passed to
 * the player's thread first, as libreforge effects are not thread-safe.
 */
internal object PlotSquaredEventListener {
    /*
    Auto claims are handled separately, as claiming several plots at once with
    /plot auto fires this before the plots are given an owner.
     */
    @Subscribe
    fun onClaim(event: PlotClaimedNotifyEvent) {
        if (event.wasAuto()) {
            return
        }

        dispatchForUuid(event.plot.owner, event, TriggerPsClaimPlot)
    }

    @Subscribe
    fun onAutoClaim(event: PostPlayerAutoPlotEvent) {
        dispatchForPlotPlayer(event.plotPlayer, event, TriggerPsClaimPlot)
    }

    @Subscribe
    fun onMerge(event: PostPlotMergeEvent) {
        dispatchForPlotPlayer(
            event.plotPlayer,
            event,
            TriggerPsMergePlots,
            value = event.plot.connectedPlots.size.toDouble()
        )
    }

    @Subscribe
    fun onUnlink(event: PostPlotUnlinkEvent) {
        dispatchForUuid(event.plot.owner, event, TriggerPsUnlinkPlots)
    }

    @Subscribe
    fun onEnter(event: PlayerEnterPlotEvent) {
        dispatchForPlotPlayer(event.plotPlayer, event, TriggerPsEnterPlot)
    }

    @Subscribe
    fun onLeave(event: PlayerLeavePlotEvent) {
        dispatchForPlotPlayer(event.plotPlayer, event, TriggerPsLeavePlot)
    }

    @Subscribe
    fun onClear(event: PostPlotClearEvent) {
        dispatchForPlotPlayer(event.plotPlayer, event, TriggerPsClearPlot)
    }

    @Subscribe
    fun onBuy(event: PostPlayerBuyPlotEvent) {
        dispatchForPlotPlayer(event.plotPlayer, event, TriggerPsBuyPlot, value = event.price())
    }

    @Subscribe
    fun onTrusted(event: PostPlayerPlotTrustedEvent) {
        handleAddRemove(event, TriggerPsTrustedOnPlot)
    }

    @Subscribe
    fun onAdded(event: PostPlayerPlotAddedEvent) {
        handleAddRemove(event, TriggerPsAddedToPlot)
    }

    private fun handleAddRemove(event: PostPlayerPlotAddRemoveEvent, trigger: Trigger) {
        if (!event.added()) {
            return
        }

        dispatchForUuid(
            event.player,
            event,
            trigger,
            victim = event.initiator?.platformPlayer as? Player
        )
    }

    private fun dispatchForPlotPlayer(
        plotPlayer: PlotPlayer<*>?,
        event: PlotEvent,
        trigger: Trigger,
        value: Double = 1.0
    ) = dispatchForUuid(plotPlayer?.uuid, event, trigger, value = value)

    private fun dispatchForUuid(
        uuid: UUID?,
        event: PlotEvent,
        trigger: Trigger,
        victim: Player? = null,
        value: Double = 1.0
    ) {
        val player = Bukkit.getPlayer(uuid ?: return) ?: return

        plugin.scheduler.on(player).run {
            trigger.dispatch(
                player.toDispatcher(),
                TriggerData(
                    player = player,
                    victim = victim,
                    location = player.location,
                    event = PlotSquaredEventWrapper(event),
                    text = event.plot?.id?.toString(),
                    value = value
                )
            )
        }
    }
}
