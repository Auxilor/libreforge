package com.willfp.libreforge.biomes

import org.bukkit.block.Biome

interface NamedBiome {
    val name: String
}

class VanillaNamedBiome(
    biome: Biome
) : NamedBiome {
    override val name = biome.key.key
}
