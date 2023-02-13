package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.fast.FastItemStack
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.event.DropResult
import com.willfp.libreforge.triggers.event.EditableBlockDropEvent
import com.willfp.libreforge.triggers.event.EditableDropEvent
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
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
        val iterator = Bukkit.recipeIterator()
        while (iterator.hasNext()) {
            val recipe = iterator.next()
            if (recipe !is FurnaceRecipe) {
                continue
            }
            val xp = ceil(recipe.experience).toInt()
            recipes[recipe.input.type] = Pair(recipe.result.type, xp)
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
        val fortune = FastItemStack.wrap(player.inventory.itemInMainHand)
            .getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS, false)

        event.addModifier {
            var (type, xp) = getOutput(it.type)
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
        item.type = type
    }
}
