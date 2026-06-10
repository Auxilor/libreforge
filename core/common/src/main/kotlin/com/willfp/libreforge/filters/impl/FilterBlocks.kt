package com.willfp.libreforge.filters.impl

import com.willfp.eco.core.blocks.Blocks
import com.willfp.eco.core.blocks.TestableBlock
import com.willfp.eco.core.blocks.matches
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.containsIgnoreCase
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.triggers.TriggerData

object FilterBlocks : Filter<Collection<TestableBlock>, Collection<String>>("blocks") {
    override val description = "Matches when the block type is in the given list."
    override val categories = setOf("world")
    override val valueType = ArgType.BLOCK_LIST
    override val additionalInfo = listOf("Passes automatically when no block is present in the trigger data.")

    override fun getValue(config: Config, data: TriggerData?, key: String): Collection<String> {
        return config.getStrings(key)
    }

    override fun isMet(data: TriggerData, value: Collection<String>, compileData: Collection<TestableBlock>): Boolean {
        val block = data.block ?: return true
        return value.containsIgnoreCase(block.type.name)
                || compileData.matches(block)
    }

    override fun makeCompileData(
        config: Config, context: ViolationContext, values: Collection<String>
    ): Collection<TestableBlock> {
        return values.map { Blocks.lookup(it) }
    }
}
