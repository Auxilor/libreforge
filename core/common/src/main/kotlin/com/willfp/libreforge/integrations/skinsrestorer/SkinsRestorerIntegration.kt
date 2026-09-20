package com.willfp.libreforge.integrations.skinsrestorer

import com.willfp.eco.core.EcoPlugin
import com.willfp.libreforge.conditions.Conditions
import com.willfp.libreforge.effects.Effects
import com.willfp.libreforge.filters.Filters
import com.willfp.libreforge.integrations.LoadableIntegration
import com.willfp.libreforge.integrations.skinsrestorer.impl.ConditionSrHasCape
import com.willfp.libreforge.integrations.skinsrestorer.impl.ConditionSrHasSkin
import com.willfp.libreforge.integrations.skinsrestorer.impl.ConditionSrSkinIs
import com.willfp.libreforge.integrations.skinsrestorer.impl.ConditionSrSkinTextureHashIs
import com.willfp.libreforge.integrations.skinsrestorer.impl.ConditionSrSkinTypeIs
import com.willfp.libreforge.integrations.skinsrestorer.impl.ConditionSrSkinUrlMatches
import com.willfp.libreforge.integrations.skinsrestorer.impl.ConditionSrSkinVariantIs
import com.willfp.libreforge.integrations.skinsrestorer.impl.EffectSrCancelSkinApply
import com.willfp.libreforge.integrations.skinsrestorer.impl.EffectSrClearSkin
import com.willfp.libreforge.integrations.skinsrestorer.impl.EffectSrCopySkin
import com.willfp.libreforge.integrations.skinsrestorer.impl.EffectSrGenerateSkin
import com.willfp.libreforge.integrations.skinsrestorer.impl.EffectSrRefreshSkin
import com.willfp.libreforge.integrations.skinsrestorer.impl.EffectSrSetAppliedSkin
import com.willfp.libreforge.integrations.skinsrestorer.impl.EffectSrSetSkin
import com.willfp.libreforge.integrations.skinsrestorer.impl.EffectSrSetSkinFromTexture
import com.willfp.libreforge.integrations.skinsrestorer.impl.EffectSrStealSkin
import com.willfp.libreforge.integrations.skinsrestorer.impl.EffectSrUpdateSkinData
import com.willfp.libreforge.integrations.skinsrestorer.impl.FilterSrHasCape
import com.willfp.libreforge.integrations.skinsrestorer.impl.FilterSrSkinName
import com.willfp.libreforge.integrations.skinsrestorer.impl.FilterSrSkinTextureHash
import com.willfp.libreforge.integrations.skinsrestorer.impl.FilterSrSkinType
import com.willfp.libreforge.integrations.skinsrestorer.impl.FilterSrSkinVariant
import com.willfp.libreforge.integrations.skinsrestorer.impl.MutatorSrSkinHashToText
import com.willfp.libreforge.integrations.skinsrestorer.impl.MutatorSrSkinNameToText
import com.willfp.libreforge.integrations.skinsrestorer.impl.MutatorSrSkinUrlToText
import com.willfp.libreforge.integrations.skinsrestorer.impl.MutatorSrVictimSkinHashToText
import com.willfp.libreforge.integrations.skinsrestorer.impl.TriggerSrApplySkin
import com.willfp.libreforge.mutators.Mutators
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Triggers
import com.willfp.libreforge.updateEffects
import net.skinsrestorer.api.event.SkinApplyEvent
import org.bukkit.entity.Player

object SkinsRestorerIntegration : LoadableIntegration {
    private var isSubscribed = false

    override fun load(plugin: EcoPlugin) {
        Triggers.register(TriggerSrApplySkin)

        Effects.register(EffectSrSetSkin)
        Effects.register(EffectSrSetSkinFromTexture)
        Effects.register(EffectSrClearSkin)
        Effects.register(EffectSrRefreshSkin)
        Effects.register(EffectSrCopySkin)
        Effects.register(EffectSrStealSkin)
        Effects.register(EffectSrUpdateSkinData)
        Effects.register(EffectSrGenerateSkin)
        Effects.register(EffectSrCancelSkinApply)
        Effects.register(EffectSrSetAppliedSkin)

        Conditions.register(ConditionSrHasSkin)
        Conditions.register(ConditionSrSkinIs)
        Conditions.register(ConditionSrSkinTypeIs)
        Conditions.register(ConditionSrSkinVariantIs)
        Conditions.register(ConditionSrSkinTextureHashIs)
        Conditions.register(ConditionSrSkinUrlMatches)
        Conditions.register(ConditionSrHasCape)

        Filters.register(FilterSrSkinType)
        Filters.register(FilterSrSkinVariant)
        Filters.register(FilterSrSkinTextureHash)
        Filters.register(FilterSrSkinName)
        Filters.register(FilterSrHasCape)

        Mutators.register(MutatorSrSkinHashToText)
        Mutators.register(MutatorSrSkinUrlToText)
        Mutators.register(MutatorSrSkinNameToText)
        Mutators.register(MutatorSrVictimSkinHashToText)

        subscribeToSkinChanges(plugin)
    }

    /*

    The sr_* conditions read the player's stored skin, so holders have to be
    refreshed whenever SkinsRestorer applies a new one. SkinApplyEvent isn't a
    bukkit event, so this can't be a listener on the conditions themselves.

     */

    private fun subscribeToSkinChanges(plugin: EcoPlugin) {
        if (isSubscribed) {
            return
        }

        val api = skinsRestorer ?: return

        isSubscribed = true

        api.eventBus.subscribe(plugin, SkinApplyEvent::class.java) { event ->
            val player = runCatching { event.getPlayer(Player::class.java) }.getOrNull()
                ?: return@subscribe

            invalidateSkinCache(player.uniqueId)

            plugin.scheduler.run {
                invalidateSkinCache(player.uniqueId)
                player.toDispatcher().updateEffects()
            }
        }
    }

    override fun getPluginName(): String {
        return "SkinsRestorer"
    }
}
