package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.Recipe
import org.bukkit.inventory.ShapedRecipe
import org.bukkit.inventory.ShapelessRecipe

object EffectCondenseItems : Effect<NoCompileData>("condense_items") {
    override val description =
        "Condenses items in the player's inventory into their storage blocks, e.g. iron ingots into iron blocks."
    override val categories = setOf("player", "inventory")

    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    /*
    Ingredient material -> how many of it are needed, and what they turn into.

    Only round-trip recipes are kept: the server must have both a recipe turning N of the
    ingredient into the result, and one turning the result back into N of the ingredient.
    That admits ingots to blocks and wheat to hay bales, while rejecting things like four
    planks to a crafting table, which is a craft rather than a condense.
     */
    private val condensable by lazy { findCondensableRecipes() }

    @Suppress("DEPRECATION")
    private fun ingredientsOf(recipe: Recipe): List<ItemStack>? = when (recipe) {
        is ShapedRecipe -> recipe.shape
            .flatMap { row -> row.toCharArray().toList() }
            .mapNotNull { recipe.ingredientMap[it] }

        is ShapelessRecipe -> recipe.ingredientList
        else -> null
    }

    private fun findCondensableRecipes(): Map<Material, Pair<Int, ItemStack>> {
        val packing = mutableMapOf<Material, Pair<Int, ItemStack>>()
        val unpacking = mutableMapOf<Material, Pair<Material, Int>>()

        val iterator = Bukkit.recipeIterator()

        while (iterator.hasNext()) {
            val recipe = iterator.next()
            val result = runCatching { recipe.result }.getOrNull() ?: continue
            val ingredients = ingredientsOf(recipe)?.filter { it.type != Material.AIR } ?: continue

            if (ingredients.isEmpty()) {
                continue
            }

            val ingredient = ingredients.first().type

            if (ingredients.any { it.type != ingredient } || result.type == ingredient) {
                continue
            }

            if (ingredients.size in 2..9 && result.amount == 1) {
                val existing = packing[ingredient]

                if (existing == null || ingredients.size > existing.first) {
                    packing[ingredient] = ingredients.size to result
                }
            }

            if (ingredients.size == 1 && result.amount > 1) {
                unpacking[ingredient] = result.type to result.amount
            }
        }

        return packing.filter { (material, entry) ->
            unpacking[entry.second.type] == material to entry.first
        }
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val player = data.player ?: return false
        val inventory = player.inventory

        var condensed = false

        for ((material, entry) in condensable) {
            val (required, result) = entry

            // Only plain items are condensed, so custom items keep their metadata.
            val slots = (0..35).filter {
                val item = inventory.getItem(it)
                item != null && item.type == material && !item.hasItemMeta()
            }

            val produced = slots.sumOf { inventory.getItem(it)?.amount ?: 0 } / required

            if (produced <= 0) {
                continue
            }

            var toTake = produced * required

            for (slot in slots) {
                if (toTake <= 0) {
                    break
                }

                val item = inventory.getItem(slot) ?: continue
                val taken = minOf(toTake, item.amount)

                item.amount -= taken
                inventory.setItem(slot, if (item.amount <= 0) null else item)
                toTake -= taken
            }

            var remaining = produced * result.amount
            val stacks = mutableListOf<ItemStack>()

            while (remaining > 0) {
                val stack = result.clone()
                stack.amount = minOf(remaining, stack.maxStackSize)
                stacks += stack
                remaining -= stack.amount
            }

            for (leftover in inventory.addItem(*stacks.toTypedArray()).values) {
                player.world.dropItem(player.location, leftover)
            }

            condensed = true
        }

        return condensed
    }
}
