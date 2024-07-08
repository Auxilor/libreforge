package com.willfp.libreforge.integrations.custombiomes.impl

import com.dfsek.terra.api.world.biome.Biome
import com.dfsek.terra.bukkit.world.BukkitAdapter
import com.willfp.eco.core.EcoPlugin
import com.willfp.libreforge.integrations.custombiomes.CustomBiomesIntegration
import com.willfp.libreforge.integrations.custombiomes.NamedBiome
import com.willfp.libreforge.integrations.custombiomes.customBiomesIntegrations
import org.bukkit.Location
import org.terraform.biome.BiomeBank
import org.terraform.coregen.bukkit.TerraformGenerator
import org.terraform.data.TerraformWorld

object CustomBiomesTerraformGenerator : CustomBiomesIntegration {
    override fun getPluginName(): String {
        return "TerraformGenerator"
    }

    override fun getBiome(location: Location?): NamedBiome? {
        if (location == null || location.world == null) {
            return null
        }

        val biome = TerraformWorld.get(location.world).getBiomeBank(location.blockX, location.blockY, location.blockZ)
        return TerraformNamedBiome(biome)
    }

    override fun load(plugin: EcoPlugin) {
        customBiomesIntegrations.register(this)
    }

    private class TerraformNamedBiome(
        biome: BiomeBank
    ) : NamedBiome {
        override val name: String = biome.name
    }
}
