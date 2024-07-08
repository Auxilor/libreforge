package com.willfp.libreforge.integrations.custombiomes

import org.bukkit.block.Biome

interface NamedBiome {
    val name: String
}

class VanillaNamedBiome(
    biome: Biome
) : NamedBiome {
    override val name = biome.name
}
