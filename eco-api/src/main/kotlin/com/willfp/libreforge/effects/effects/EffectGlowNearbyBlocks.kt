package com.willfp.libreforge.effects.effects

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.TeamUtils
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getIntFromExpression
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.Triggers
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.EntityType
import org.bukkit.entity.Shulker
import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.scoreboard.Team
import java.util.*

class EffectGlowNearbyBlocks : Effect(
    "glow_nearby_blocks",
    triggers = Triggers.withParameters(
        TriggerParameter.LOCATION
    )
) {
    override fun handle(data: TriggerData, config: Config) {
        val location = data.location ?: return

        val radius = config.getIntFromExpression("radius", data)

        val duration = config.getIntFromExpression("duration", data)

        val colors = config.getSubsection("colors").getKeys(false).associate {
            val material =
                Material.matchMaterial(it.uppercase()) ?: Material.APPLE // Why apple? Because it's not a block.

            val color = runCatching { ChatColor.valueOf(config.getString("colors.$it").uppercase()) }.getOrNull()
                ?: ChatColor.WHITE // Safe default

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
            shulker.setMetadata("gnb-shulker", this.plugin.metadataValueFactory.create(true))
            block.setMetadata("gnb-uuid", this.plugin.metadataValueFactory.create(shulker.uniqueId))

            this.plugin.scheduler.runLater(duration.toLong()) {
                shulker.remove()
                block.removeMetadata("gnb-uuid", this.plugin)
            }
        }
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

    override fun validateConfig(config: Config): List<ConfigViolation> {
        val violations = mutableListOf<ConfigViolation>()

        if (!config.has("radius")) violations.add(
            ConfigViolation(
                "radius",
                "You must specify the radius!"
            )
        )

        if (!config.has("duration")) violations.add(
            ConfigViolation(
                "duration",
                "You must specify the duration to glow for!"
            )
        )

        if (!config.has("colors")) violations.add(
            ConfigViolation(
                "colors",
                "You must specify the block colors! " +
                        "(See the wiki for the format: https://plugins.auxilor.io/effects/all-effects/glow_nearby_blocks)"
            )
        )

        return violations
    }
}
