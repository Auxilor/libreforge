package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.fast.FastItemStack
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getEnchantment
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.event.DropCause
import com.willfp.libreforge.triggers.event.DropResult
import com.willfp.libreforge.triggers.event.EditableDropEvent
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.FurnaceRecipe
import org.bukkit.inventory.ItemStack
import kotlin.math.ceil

object EffectAutosmelt : Effect<NoCompileData>("autosmelt") {
    override val description = "Automatically smelts blocks as they are mined, converting drops to their furnace output."
    override val categories = setOf("world", "inventory")

    override val isPermanent = false

    override val arguments = arguments {
        require(
            "drop_xp",
            "You must specify if xp should be dropped!",
            description = "Whether to drop smelting XP alongside the smelted item.",
            type = ArgType.BOOLEAN
        )
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
            @Suppress("DEPRECATION")
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
            .getEnchantmentLevel(getEnchantment("fortune")!!, false)

        event.addModifier { drop ->
            var (type, xp) = getOutput(drop.type)
            @Suppress("DEPRECATION")
            drop.type = type

            if (fortune > 0 && isFortuneDrop(drop, type, event)) {
                drop.amount = drop.amount.coerceAtLeast(fortuneDropAmount(fortune))
                xp++
            }

            if (!config.getBool("drop_xp")) {
                xp = 0
            }

            DropResult(drop, xp)
        }
    }

    private fun isFortuneDrop(drop: ItemStack, type: Material, event: EditableDropEvent): Boolean =
        drop.maxStackSize > 1 && event.cause == DropCause.BLOCK && type in fortuneMaterials

    private fun handleItem(item: ItemStack) {
        val (type, _) = getOutput(item.type)
        @Suppress("DEPRECATION")
        item.type = type
    }
}
