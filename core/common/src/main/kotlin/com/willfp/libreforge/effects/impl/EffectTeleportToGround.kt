package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.Regions
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.Material
import org.bukkit.block.BlockFace

object EffectTeleportToGround : Effect<NoCompileData>("teleport_to_ground") {
    override val description = "Teleports the player straight down to the first solid ground beneath them. Does nothing if that ground is lava, fire or another harmful block."
    override val categories = setOf("movement")

    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    private val harmfulBlocks = setOf(
        Material.LAVA,
        Material.FIRE,
        Material.SOUL_FIRE,
        Material.MAGMA_BLOCK,
        Material.CACTUS,
        Material.CAMPFIRE,
        Material.SOUL_CAMPFIRE,
        Material.POWDER_SNOW,
        Material.SWEET_BERRY_BUSH,
        Material.WITHER_ROSE
    )

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val player = data.player ?: return false
        val location = player.location
        val world = location.world ?: return false

        var block = world.getBlockAt(
            location.blockX,
            location.blockY.coerceAtMost(world.maxHeight - 1),
            location.blockZ
        )

        while (block.isPassable && block.type !in harmfulBlocks) {
            if (block.y <= world.minHeight) {
                return false
            }
            block = block.getRelative(BlockFace.DOWN)
        }

        if (block.type in harmfulBlocks) {
            return false
        }

        val collisionTop = block.boundingBox.maxY

        Regions.teleport(player, location.clone().apply {
            y = if (collisionTop > block.y) collisionTop else block.y + 1.0
        })

        return true
    }
}
