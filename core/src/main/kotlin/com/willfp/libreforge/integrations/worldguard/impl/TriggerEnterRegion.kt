package com.willfp.libreforge.integrations.worldguard.impl

import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldguard.WorldGuard
import com.willfp.eco.core.Prerequisite
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerMoveEvent

object TriggerEnterRegion : Trigger("enter_region") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION,
        TriggerParameter.EVENT
    )

    private val regionContainer = WorldGuard.getInstance().platform.regionContainer

    @EventHandler
    fun handle(event: PlayerMoveEvent) {
        val player = event.player

        if (Prerequisite.HAS_PAPER.isMet && !event.hasChangedBlock()) {
            return
        }

        @Suppress("USELESS_ELVIS")
        val to = event.to ?: return
        val from = event.from

        val old = mutableSetOf<String>()
        val new = mutableSetOf<String>()


        val wgTo = BukkitAdapter.adapt(to).toVector().toBlockPoint()
        val wgFrom = BukkitAdapter.adapt(from).toVector().toBlockPoint()

        if (event.from.world != null) {
            val world = BukkitAdapter.adapt(event.from.world)
            val manager = regionContainer[world] ?: return
            old.addAll(manager.getApplicableRegionsIDs(wgFrom))
        }

        if (event.to.world != null) {
            val world = BukkitAdapter.adapt(event.to.world)
            val manager = regionContainer[world] ?: return
            new.addAll(manager.getApplicableRegionsIDs(wgTo))
        }

        val entered = new - old
        val left = old - new

        for (region in entered) {
            this.dispatch(
                player,
                TriggerData(
                    player = player,
                    location = to,
                    event = RegionEvent(player, to, region)
                )
            )
        }

        for (region in left) {
            TriggerLeaveRegion.dispatch(
                player,
                RegionEvent(player, to, region)
            )
        }
    }
}
