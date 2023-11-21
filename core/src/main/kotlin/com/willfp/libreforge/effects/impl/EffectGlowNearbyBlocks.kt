@file:Suppress("DEPRECATION")

package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.TeamUtils
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.enumValueOfOrNull
import com.willfp.libreforge.getIntFromExpression
import com.willfp.libreforge.plugin
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Shulker
import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.world.ChunkLoadEvent
import org.bukkit.event.world.ChunkUnloadEvent
import org.bukkit.scoreboard.Team
import java.util.UUID

object EffectGlowNearbyBlocks : Effect<NoCompileData>("glow_nearby_blocks") {
    override val parameters = setOf(
        TriggerParameter.LOCATION
    )

    override val arguments = arguments {
        require("radius", "You must specify the radius!")
        require("duration", "You must specify the duration to glow for!")
        require("colors", "You must specify the block colors!")
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val location = data.location ?: return false

        val radius = config.getIntFromExpression("radius", data)

        val duration = config.getIntFromExpression("duration", data)

        val colors = config.getSubsection("colors").getKeys(false).associate {
            val material =
                Material.matchMaterial(it.uppercase()) ?: Material.APPLE // Why apple? Because it's not a block.

            val color = enumValueOfOrNull(config.getString("colors.$it").uppercase())
                ?: ChatColor.WHITE

            Pair(material, color)
        }

        val toReveal = mutableMapOf<Block, Team>()

        for (x in -radius..radius) {
            for (y in -radius..radius) {
                for (z in -radius..radius) {
                    val block = location.world.getBlockAt(
                        location.clone().add(
                            x.toDouble(),
                            y.toDouble(),
                            z.toDouble()
                        )
                    )

                    val color = colors[block.type] ?: continue

                    toReveal[block] = TeamUtils.fromChatColor(color)
                }
            }
        }

        for ((block, team) in toReveal) {
            val shulker = block.world.spawnEntity(block.location, EntityType.SHULKER) as Shulker
            shulker.isInvulnerable = true
            shulker.isSilent = true
            shulker.setAI(false)
            shulker.setGravity(false)
            shulker.isGlowing = true
            shulker.isInvisible = true
            shulker.setMetadata("gnb-shulker", plugin.metadataValueFactory.create(true))
            team.addEntry(shulker.uniqueId.toString())
            block.setMetadata("gnb-uuid", plugin.metadataValueFactory.create(shulker.uniqueId))

            plugin.scheduler.runLater(duration.toLong()) {
                shulker.remove()
                block.removeMetadata("gnb-uuid", plugin)
            }
        }

        return true
    }

    @EventHandler
    fun handleChunkUnload(event: ChunkUnloadEvent) {
        event.chunk.entities.filterIsInstance<LivingEntity>()
            .filter { it.hasMetadata("gnb-shulker") }
            .forEach { it.remove() }
    }

    @EventHandler
    fun handleChunkLoad(event: ChunkLoadEvent) {
        event.chunk.entities.filterIsInstance<LivingEntity>()
            .filter { it.hasMetadata("gnb-shulker") }
            .forEach { it.remove() }
    }

    @EventHandler
    fun onBreak(event: BlockBreakEvent) {
        val block = event.block

        if (!block.hasMetadata("gnb-uuid")) {
            return
        }

        val uuid = block.getMetadata("gnb-uuid").firstOrNull {
            it.value() is UUID
        }?.value() as? UUID ?: return

        Bukkit.getServer().getEntity(uuid)?.remove()

        for (shulker in block.location.world.getNearbyEntities(
            block.location,
            2.0,
            2.0,
            2.0
        ) { it.hasMetadata("gnb-shulker") }) {
            shulker.remove()
        }
    }
}
