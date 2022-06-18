package com.willfp.libreforge.effects.effects

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.fast.FastItemStack
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.Triggers
import com.willfp.libreforge.triggers.wrappers.WrappedBlockDropEvent
import com.willfp.libreforge.triggers.wrappers.WrappedDropEvent
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.FurnaceRecipe
import org.bukkit.inventory.ItemStack
import kotlin.math.ceil
import kotlin.math.roundToInt

class EffectAutosmelt : Effect(
    "autosmelt",
    applicableTriggers = Triggers.withParameters(
        TriggerParameter.PLAYER
    )
) {
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

    override fun handle(data: TriggerData, config: Config) {
        val player = data.player ?: return
        val event = data.event as? WrappedDropEvent<*>
        val item = data.item

        if (event != null) {
            handleEvent(player, event, config)
        }

        if (item != null) {
            handleItem(item)
        }
    }

    private fun handleEvent(player: Player, event: WrappedDropEvent<*>, config: Config) {
        val fortune = FastItemStack.wrap(player.inventory.itemInMainHand)
            .getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS, false)

        event.modifier = {
            var (type, xp) = getOutput(it.type)
            it.type = type

            if (fortune > 0 && it.maxStackSize > 1 && event is WrappedBlockDropEvent && fortuneMaterials.contains(type)) {
                it.amount = (Math.random() * (fortune.toDouble() - 1) + 1.1).roundToInt()
                xp++
            }

            if (!config.getBool("drop_xp")) {
                xp = 0
            }

            Pair(it, xp)
        }
    }

    private fun handleItem(item: ItemStack) {
        val (type, _) = getOutput(item.type)
        item.type = type
    }

    override fun validateConfig(config: Config): List<ConfigViolation> {
        val violations = mutableListOf<ConfigViolation>()

        if (!config.has("drop_xp")) violations.add(
            ConfigViolation(
                "drop_xp",
                "You must specify if xp should be dropped!"
            )
        )

        return violations
    }
}
