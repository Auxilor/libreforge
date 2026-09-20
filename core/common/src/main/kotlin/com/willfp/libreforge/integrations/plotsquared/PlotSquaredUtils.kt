package com.willfp.libreforge.integrations.plotsquared

import com.plotsquared.core.player.PlotPlayer
import com.plotsquared.core.plot.Plot
import com.plotsquared.core.plot.PlotArea
import org.bukkit.entity.Player

/**
 * The PlotSquared player for a player, or null if PlotSquared does not know about them.
 */
internal val Player.plotPlayer: PlotPlayer<*>?
    get() = runCatching { PlotPlayer.from(this) }.getOrNull()

/**
 * The plot area the player is standing in, if they are in a plot world.
 */
internal val Player.plotArea: PlotArea?
    get() = this.plotPlayer?.location?.plotArea

/**
 * The plot the player is standing in, if they are in one.
 *
 * Unclaimed plots are included, roads are not.
 */
internal val Player.currentPlot: Plot?
    get() = this.plotPlayer?.currentPlot
