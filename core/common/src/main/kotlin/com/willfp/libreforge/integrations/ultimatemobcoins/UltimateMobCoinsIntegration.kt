package com.willfp.libreforge.integrations.ultimatemobcoins

import com.willfp.eco.core.EcoPlugin
import com.willfp.libreforge.effects.Effects
import com.willfp.libreforge.integrations.LoadableIntegration
import com.willfp.libreforge.integrations.ultimatemobcoins.impl.EffectGiveMobCoins
import com.willfp.libreforge.integrations.ultimatemobcoins.impl.EffectMobCoinsChanceMultiplier
import com.willfp.libreforge.integrations.ultimatemobcoins.impl.EffectMobCoinsDropMultiplier
import com.willfp.libreforge.integrations.ultimatemobcoins.impl.EffectTakeMobCoins
import nl.chimpgamer.ultimatemobcoins.paper.UltimateMobCoinsPlugin
import org.bukkit.Bukkit

object UltimateMobCoinsIntegration: LoadableIntegration {
    override fun load(plugin: EcoPlugin) {
        Effects.register(EffectMobCoinsDropMultiplier)
        Effects.register(EffectMobCoinsChanceMultiplier)

        val plugin = Bukkit.getPluginManager().getPlugin("UltimateMobCoins") ?: return
        if (plugin !is UltimateMobCoinsPlugin) return
        Effects.register(EffectGiveMobCoins(plugin))
        Effects.register(EffectTakeMobCoins(plugin))
    }

    override fun getPluginName(): String = "UltimateMobCoins"
}