package com.willfp.libreforge.integrations.worldguard.impl

import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldedit.math.BlockVector3
import com.sk89q.worldguard.WorldGuard
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.containsIgnoreCase
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import org.bukkit.entity.Player

object ConditionInRegion : Condition<NoCompileData>("in_region") {
    override val arguments = arguments {
        require("region", "You must specify the allowed region names!")
    }

    private val regionContainer = WorldGuard.getInstance().platform.regionContainer

    override fun isMet(player: Player, config: Config, compileData: NoCompileData): Boolean {
        val world = BukkitAdapter.adapt(player.world)
        val block = BlockVector3.at(player.location.x, player.location.y, player.location.z)
        val manager = regionContainer[world] ?: return false

        val inRegions = manager.getApplicableRegionsIDs(block).map { it.lowercase() }.toSet()
        val allowedRegions = config.getStrings("region").map { it.lowercase() }.toSet()

        return inRegions.intersect(allowedRegions).isNotEmpty()
    }
}
