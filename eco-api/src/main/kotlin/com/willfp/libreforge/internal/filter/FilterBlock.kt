package com.willfp.libreforge.internal.filter

import com.willfp.eco.core.config.interfaces.JSONConfig
import com.willfp.libreforge.api.effects.ConfigViolation
import com.willfp.libreforge.api.filter.Filter
import org.bukkit.block.Block
import java.util.function.Predicate

class FilterBlock(
    filterConfig: JSONConfig
) : Filter() {
    private val typeFilter = Predicate<Block> { block ->
        val materialNames = filterConfig.getStringsOrNull("blocks", false)
            ?.map { it.uppercase() } ?: emptyList()

        return@Predicate materialNames.contains(block.type.name)
    }

    override fun matches(obj: Any): Boolean {
        if (obj !is Block) {
            return false
        }

        return typeFilter.test(obj)
    }

    override fun validateConfig(config: JSONConfig): List<ConfigViolation> {
        return emptyList()
    }
}
