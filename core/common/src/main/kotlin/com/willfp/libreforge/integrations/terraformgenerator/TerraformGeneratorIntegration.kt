package com.willfp.libreforge.integrations.terraformgenerator

import com.willfp.eco.core.EcoPlugin
import com.willfp.libreforge.biomes.BiomeProvider
import com.willfp.libreforge.biomes.NamedBiome
import com.willfp.libreforge.biomes.biomeProviders
import org.bukkit.Location
import org.terraform.biome.BiomeBank
import org.terraform.data.TerraformWorld

object TerraformGeneratorIntegration : BiomeProvider {
    override fun getPluginName(): String = "TerraformGenerator"

    override fun getBiome(location: Location?): NamedBiome? {
        if (location == null || location.world == null) {
            return null
        }

        val biome = TerraformWorld.get(location.world).getBiomeBank(location.blockX, location.blockY, location.blockZ)
        return TerraformNamedBiome(biome)
    }

    override fun load(plugin: EcoPlugin) {
        biomeProviders.register(this)
    }

    private class TerraformNamedBiome(
        biome: BiomeBank
    ) : NamedBiome {
        override val name: String = biome.name
    }
}
