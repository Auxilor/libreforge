package com.willfp.libreforge.integrations.notbounties

import com.willfp.libreforge.plugin
import me.jadenp.notbounties.bounty_events.BountyClaimEvent
import me.jadenp.notbounties.utils.BountyManager
import org.bukkit.entity.Player
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.plugin.RegisteredListener

internal val Player.totalBounty: Double
    get() = BountyManager.getBounty(this.uniqueId)?.totalDisplayBounty ?: 0.0

/**
 * Listen for [BountyClaimEvent].
 *
 * BountyClaimEvent has no static getHandlerList, so Bukkit rejects a listener registered
 * with @EventHandler. Registering against the handler list itself works on every version.
 */
internal fun Listener.listenForBountyClaim(handle: (BountyClaimEvent) -> Unit) {
    BountyClaimEvent(null, null).handlers.register(
        RegisteredListener(
            this,
            { _, event -> handle(event as BountyClaimEvent) },
            EventPriority.MONITOR,
            plugin,
            true
        )
    )
}
