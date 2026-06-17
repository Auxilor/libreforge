package com.willfp.libreforge.integrations.custom_blocks.itemsadder

import com.willfp.eco.core.EcoPlugin
import com.willfp.libreforge.effects.Effects
import com.willfp.libreforge.integrations.LoadableIntegration
import com.willfp.libreforge.integrations.custom_blocks.itemsadder.impl.EffectItemsAdderTelekinesis
import com.willfp.libreforge.integrations.custom_blocks.itemsadder.impl.TriggerItemsAdderBlockItemDrop

object ItemsAdderIntegration : LoadableIntegration {
    override fun load(plugin: EcoPlugin) {
        Effects.register(EffectItemsAdderTelekinesis)
        plugin.eventManager.registerListener(TriggerItemsAdderBlockItemDrop)
    }

    override fun getPluginName(): String {
        return "ItemsAdder"
    }
}
