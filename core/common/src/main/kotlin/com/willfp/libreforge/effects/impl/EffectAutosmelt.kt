package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getEnchantment
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.event.DropResult
import com.willfp.libreforge.triggers.event.EditableBlockDropEvent
import com.willfp.libreforge.triggers.event.EditableDropEvent
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.FurnaceRecipe
import org.bukkit.inventory.ItemStack
import kotlin.math.ceil
import kotlin.math.roundToInt

object EffectAutosmelt : Effect<NoCompileData>("autosmelt") {
    override val isPermanent = false

    override val arguments = arguments {
        require("drop_xp", "You must specify if xp should be dropped!")
    }

    private val recipes = mutableMapOf<Material, Pair<Material, Int>>()

    private val fortuneMaterials = mutableSetOf(
        Material.GOLD_INGOT,
        Material.IRON_INGOT,
        Material.COPPER_INGOT
    )

    init {
        loadRecipes()
    }

    private fun loadRecipes() {
        recipes.clear()
        
        // Load recipes from Bukkit
        val iterator = Bukkit.recipeIterator()
        while (iterator.hasNext()) {
            val recipe = iterator.next()
            if (recipe !is FurnaceRecipe) {
                continue
            }
            val xp = ceil(recipe.experience).toInt()
            @Suppress("DEPRECATION")
            recipes[recipe.input.type] = Pair(recipe.result.type, xp)
        }

        // Add fallback recipes for common ores if they're missing (using vanilla XP values)
        addFallbackRecipe(Material.IRON_ORE, Material.IRON_INGOT, ceil(0.7f).toInt())
        addFallbackRecipe(Material.DEEPSLATE_IRON_ORE, Material.IRON_INGOT, ceil(0.7f).toInt())
        addFallbackRecipe(Material.GOLD_ORE, Material.GOLD_INGOT, ceil(1.0f).toInt())
        addFallbackRecipe(Material.DEEPSLATE_GOLD_ORE, Material.GOLD_INGOT, ceil(1.0f).toInt())
        addFallbackRecipe(Material.COPPER_ORE, Material.COPPER_INGOT, ceil(0.7f).toInt())
        addFallbackRecipe(Material.DEEPSLATE_COPPER_ORE, Material.COPPER_INGOT, ceil(0.7f).toInt())
        addFallbackRecipe(Material.COAL_ORE, Material.COAL, ceil(0.1f).toInt())
        addFallbackRecipe(Material.DEEPSLATE_COAL_ORE, Material.COAL, ceil(0.1f).toInt())
        addFallbackRecipe(Material.DIAMOND_ORE, Material.DIAMOND, ceil(1.0f).toInt())
        addFallbackRecipe(Material.DEEPSLATE_DIAMOND_ORE, Material.DIAMOND, ceil(1.0f).toInt())
        addFallbackRecipe(Material.EMERALD_ORE, Material.EMERALD, ceil(1.0f).toInt())
        addFallbackRecipe(Material.DEEPSLATE_EMERALD_ORE, Material.EMERALD, ceil(1.0f).toInt())
        addFallbackRecipe(Material.LAPIS_ORE, Material.LAPIS_LAZULI, ceil(0.2f).toInt())
        addFallbackRecipe(Material.DEEPSLATE_LAPIS_ORE, Material.LAPIS_LAZULI, ceil(0.2f).toInt())
        addFallbackRecipe(Material.REDSTONE_ORE, Material.REDSTONE, ceil(0.7f).toInt())
        addFallbackRecipe(Material.DEEPSLATE_REDSTONE_ORE, Material.REDSTONE, ceil(0.7f).toInt())
        addFallbackRecipe(Material.NETHER_GOLD_ORE, Material.GOLD_NUGGET, ceil(1.0f).toInt())
        addFallbackRecipe(Material.NETHER_QUARTZ_ORE, Material.QUARTZ, ceil(0.2f).toInt())
    }

    private fun addFallbackRecipe(input: Material, output: Material, xp: Int) {
        if (!recipes.containsKey(input)) {
            recipes[input] = Pair(output, xp)
        }
    }

    private fun getOutput(input: Material): Pair<Material, Int> {
        return recipes[input] ?: return Pair(input, 0)
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val player = data.player
        val event = data.event as? EditableDropEvent
        val item = data.item

        if (event != null && player != null) {
            handleEvent(player, event, config)
            return true
        } else {
            if (item != null) {
                handleItem(item)
                return true
            }
        }

        return false
    }

    private fun handleEvent(player: Player, event: EditableDropEvent, config: Config) {
        val fortune = try {
            val fortuneEnchant = getEnchantment("fortune")
            if (fortuneEnchant != null) {
                player.inventory.itemInMainHand.getEnchantmentLevel(fortuneEnchant)
            } else {
                0
            }
        } catch (_: Exception) {
            0
        }

        event.addModifier {
            var (type, xp) = getOutput(it.type)
            @Suppress("DEPRECATION")
            it.type = type

            if (fortune > 0 && it.maxStackSize > 1 && event is EditableBlockDropEvent && fortuneMaterials.contains(type)) {
                it.amount = (Math.random() * (fortune.toDouble() - 1) + 1.1).roundToInt()
                xp++
            }

            if (!config.getBool("drop_xp")) {
                xp = 0
            }

            DropResult(it, xp)
        }
    }

    private fun handleItem(item: ItemStack) {
        val (type, _) = getOutput(item.type)
        @Suppress("DEPRECATION")
        item.type = type
    }
}
