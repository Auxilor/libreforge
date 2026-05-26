package com.willfp.libreforge.integrations.terra

import com.dfsek.terra.api.world.biome.Biome
import com.dfsek.terra.bukkit.world.BukkitAdapter
import com.willfp.eco.core.EcoPlugin
import com.willfp.libreforge.biomes.BiomeProvider
import com.willfp.libreforge.biomes.NamedBiome
import com.willfp.libreforge.biomes.biomeProviders
import org.bukkit.Location

object TerraIntegration : BiomeProvider {
    override fun getPluginName(): String = "Terra"

    override fun getBiome(location: Location?): NamedBiome? {
        if (location == null || location.world == null) {
            return null
        }

        val terraLocation = BukkitAdapter.adapt(location) ?: return null
        val terraWorld = BukkitAdapter.adapt(location.world!!) ?: return null
        val biomeProvider = terraWorld.biomeProvider ?: return null
        val biome = biomeProvider.getBiome(terraLocation, terraWorld.seed) ?: return null

        return TerraNamedBiome(biome)
    }

    override fun load(plugin: EcoPlugin) {
        biomeProviders.register(this)
    }

    private class TerraNamedBiome(
        biome: Biome
    ) : NamedBiome {
        override val name: String = biome.id
    }
}
