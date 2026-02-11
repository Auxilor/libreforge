package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.blocks.Blocks
import com.willfp.eco.core.blocks.TestableBlock
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter

object EffectSetBlock : Effect<TestableBlock?>("set_block") {
    override val parameters = setOf(
        TriggerParameter.BLOCK
    )

    override val arguments = arguments {
        require("block", "You must specify the block!")
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: TestableBlock?): Boolean {
        val block = data.block ?: data.location?.block ?: return false
        compileData ?: return false

        compileData.place(block.location)

        return true
    }

    override fun makeCompileData(config: Config, context: ViolationContext): TestableBlock? {
        return Blocks.lookup(config.getString("block"))
    }
}
