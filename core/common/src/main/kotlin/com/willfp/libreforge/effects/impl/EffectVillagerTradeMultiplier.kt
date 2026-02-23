package com.willfp.libreforge.effects.impl

import com.willfp.libreforge.effects.templates.MultiplierEffect
import com.willfp.libreforge.toDispatcher
import org.bukkit.Bukkit
import org.bukkit.entity.AbstractVillager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.MerchantRecipe
import kotlin.math.ceil

object EffectVillagerTradeMultiplier : MultiplierEffect("villager_trade_multiplier"), Listener {

    @EventHandler(ignoreCancelled = true)
    fun handle(event: PlayerInteractEntityEvent) {
        val player = event.player
        val villager = event.rightClicked as? AbstractVillager ?: return

        if (villager.recipes.isEmpty()) {
            return
        }

        val multiplier = getMultiplier(player.toDispatcher())

        if (multiplier == 1.0) {
            return
        }

        event.isCancelled = true

        val discountedRecipes = villager.recipes.map { original ->
            val discountedIngredients = original.ingredients.map { ingredient ->
                val reducedAmount = ceil(ingredient.amount * multiplier).coerceAtLeast(1.0).toInt()
                ItemStack(ingredient.type, reducedAmount).also { it.itemMeta = ingredient.itemMeta }
            }

            MerchantRecipe(original.result.clone(), original.maxUses).apply {
                uses = original.uses
                villagerExperience = original.villagerExperience
                setExperienceReward(original.hasExperienceReward())
                priceMultiplier = original.priceMultiplier
                ingredients = discountedIngredients
            }
        }

        @Suppress("DEPRECATION", "REMOVAL")
        val displayName = villager.customName ?: when (villager.javaClass.simpleName) {
            "Villager" -> "Villager"
            "WanderingTrader" -> "Wandering Trader"
            else -> "Trader"
        }

        @Suppress("DEPRECATION", "REMOVAL")
        val merchant = Bukkit.createMerchant(displayName).apply {
            recipes = discountedRecipes
        }

        @Suppress("DEPRECATION", "REMOVAL")
        player.openMerchant(merchant, true)
    }
}