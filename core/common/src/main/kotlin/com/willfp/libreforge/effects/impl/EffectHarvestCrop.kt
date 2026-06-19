package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.plugin
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.block.data.Ageable

object EffectHarvestCrop : Effect<NoCompileData>("harvest_crop") {
    override val description = "Harvests the triggered crop block and resets its growth to zero."
    override val categories = setOf("world")

    override val parameters = setOf(
        TriggerParameter.BLOCK
    )

    override val arguments = arguments {
        require(
            "only_fully_grown",
            "You must specify if only fully grown crops should be harvested!",
            description = "Whether the crop must be fully grown before it is harvested.",
            type = ArgType.BOOLEAN
        )
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val block = data.block ?: return false
        val blockData = block.blockData as? Ageable ?: return false

        if (config.getBool("only_fully_grown") && blockData.age != blockData.maximumAge) return false

        block.drops.forEach { drop -> block.world.dropItemNaturally(block.location, drop) }

        plugin.scheduler.run {
            blockData.age = 0
            block.type = block.type
            block.blockData = blockData
        }

        return true
    }
}
