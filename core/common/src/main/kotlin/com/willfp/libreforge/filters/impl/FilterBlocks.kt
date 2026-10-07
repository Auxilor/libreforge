package com.willfp.libreforge.filters.impl

import com.willfp.eco.core.blocks.Blocks
import com.willfp.eco.core.blocks.TestableBlock
import com.willfp.eco.core.blocks.matches
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.filters.EnumNames
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.filters.hasEnumName
import com.willfp.libreforge.triggers.TriggerData
import org.bukkit.Material
import java.util.Locale

object FilterBlocks : Filter<Collection<TestableBlock>, Collection<String>>("blocks") {
    override val description = "Matches when the block type is in the given list."
    override val categories = setOf("world")
    override val valueType = ArgType.BLOCK_LIST
    override val additionalInfo = listOf("Passes automatically when no block is present in the trigger data.")

    override fun getValue(config: Config, data: TriggerData?, key: String): Collection<String> {
        return EnumNames(config.getStrings(key))
    }

    override fun isMet(data: TriggerData, value: Collection<String>, compileData: Collection<TestableBlock>): Boolean {
        val block = data.block ?: return true
        return value.hasEnumName(block.type.name)
                || compileData.matches(block)
    }

    override fun makeCompileData(
        config: Config, context: ViolationContext, values: Collection<String>
    ): Collection<TestableBlock> {
        // Plain material names already match by name.
        return values.filter { Material.getMaterial(it.uppercase(Locale.ROOT)) == null }.map { Blocks.lookup(it) }
    }
}
