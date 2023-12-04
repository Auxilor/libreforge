package com.willfp.libreforge.integrations.custombiomes.impl

import com.dfsek.terra.bukkit.world.BukkitAdapter
import com.willfp.eco.core.EcoPlugin
import com.willfp.libreforge.integrations.custombiomes.CustomBiome
import com.willfp.libreforge.integrations.custombiomes.CustomBiomesIntegration
import com.willfp.libreforge.integrations.custombiomes.CustomBiomesManager
import org.bukkit.Location

object CustomBiomesTerra: CustomBiomesIntegration {
    override fun getPluginName(): String {
        return "Terra"
    }

    override fun getBiome(location: Location?): CustomBiome? {
        if (location == null || location.world == null) {
            return null
        }

        val terraLocation = BukkitAdapter.adapt(location) ?: return null
        val terraWorld = BukkitAdapter.adapt(location.world!!) ?: return null
        val biomeProvider = terraWorld.biomeProvider ?: return null
        val biome = biomeProvider.getBiome(terraLocation, terraWorld.seed) ?: return null

        return CustomBiome(biome.id)
    }

    override fun load(plugin: EcoPlugin) {
        CustomBiomesManager.register(this)
    }
}