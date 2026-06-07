package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.blocks.Blocks
import com.willfp.eco.core.blocks.TestableBlock
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getIntFromExpression
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter

object EffectSetNearbyBlocks : Effect<TestableBlock>("set_nearby_blocks") {
    override val parameters = setOf(
        TriggerParameter.LOCATION
    )

    override val arguments = arguments {
        require("block", "You must specify the block type!")
        require("radius", "You must specify the radius!")
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: TestableBlock): Boolean {
        val location = data.location ?: return false
        val radius = config.getIntFromExpression("radius", data)

        for (x in -radius..radius) {
            for (y in -radius..radius) {
                for (z in -radius..radius) {
                    val blockLocation = location.clone().add(x.toDouble(), y.toDouble(), z.toDouble())
                    compileData.place(blockLocation)
                }
            }
        }

        return true
    }

    override fun makeCompileData(config: Config, context: ViolationContext): TestableBlock {
        return Blocks.lookup(config.getString("block"))
    }
}
