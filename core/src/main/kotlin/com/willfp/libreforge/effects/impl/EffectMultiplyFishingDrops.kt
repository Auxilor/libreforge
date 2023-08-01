package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.Material
import org.bukkit.entity.*
import org.bukkit.event.player.PlayerFishEvent
import org.bukkit.inventory.ItemStack
import kotlin.math.roundToInt

object EffectMultiplyFishingDrops : Effect<NoCompileData>("multiply_fishing_drops") {
    override val parameters = setOf(
        TriggerParameter.EVENT
    )

    override val arguments = arguments {
        require(listOf("multiplier"), "You must specify a multiplier to mimic!")
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val event = data.event as? PlayerFishEvent ?: return false

        // Only apply the effect when a fish is caught
        if (event.state != PlayerFishEvent.State.CAUGHT_FISH) {
            return true
        }

        // Cast the caught entity to an Item
        val caughtItemEntity = event.caught as? Item ?: return true

        println("Caught entity: ${caughtItemEntity::class.simpleName}") // Debug statement

        // Calculate the multiplier
        val multiplier = if (config.has("multiplier")) {
            config.getDoubleFromExpression("multiplier", data).roundToInt()
        } else 1

        // Cancel the original event
        event.isCancelled = true

        // Determine the material of the caught item
        val material = when (caughtItemEntity.itemStack.type) {
            Material.COD -> Material.COD
            Material.SALMON -> Material.SALMON
            Material.PUFFERFISH -> Material.PUFFERFISH
            Material.TROPICAL_FISH -> Material.TROPICAL_FISH
            else -> {
                println("Caught entity is not a recognized fish") // Debug statement
                return true
            }
        }

        // Manually drop the multiplied amount of fish at the player's location
        for (i in 1..multiplier) {
            event.player.world.dropItemNaturally(event.player.location, ItemStack(material))
        }

        return true
    }
}