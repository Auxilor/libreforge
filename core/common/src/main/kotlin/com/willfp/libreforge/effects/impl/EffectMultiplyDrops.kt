package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.items.Items
import com.willfp.eco.core.items.TestableItem
import com.willfp.eco.core.items.matches
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.getFormattedStrings
import com.willfp.libreforge.getIntFromExpression
import com.willfp.libreforge.plugin
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.event.DropResult
import com.willfp.libreforge.triggers.event.EditableDropEvent
import org.bukkit.inventory.ItemStack
import kotlin.math.floor
import kotlin.math.roundToInt
import kotlin.random.Random

object EffectMultiplyDrops : Effect<NoCompileData>("multiply_drops") {
    override val description = "Multiplies the item drops from an event, either by a flat multiplier or by simulating a fortune level."
    override val categories = setOf("inventory")
    override val additionalInfo = listOf("Requires a drop trigger: block_item_drop, entity_item_drop, catch_fish, & shear")

    override val parameters = setOf(
        TriggerParameter.EVENT
    )

    override val arguments = arguments {
        require(
            listOf("multiplier", "fortune"),
            "You must specify a multiplier or level of fortune to mimic!",
            description = "Either a flat drop multiplier or a fortune level to simulate. Supports expressions.",
            type = ArgType.EXPRESSION,
            example = "1 + %level% * 0.1"
        )
        optional(
            "on_items",
            description = "A list of items to restrict drop multiplication to. Defaults to all drops.",
            type = ArgType.ITEM_LIST,
            default = "[]"
        )
        optional(
            "fractional",
            description = "If a multiplier with a fraction, such as 1.25, gives an extra item that often instead of being rounded to a whole number.",
            type = ArgType.BOOLEAN,
            default = "false",
            example = "true"
        )
    }

    private val whitelist = mutableListOf<TestableItem>()

    override fun postRegister() {
        plugin.onReload {
            whitelist.clear()
            whitelist.addAll(plugin.configYml.getStrings("effects.multiply_drops.whitelist").map { Items.lookup(it) })
        }
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val event = data.event as? EditableDropEvent ?: return false

        val isBlacklisting = plugin.configYml.getBool("effects.multiply_drops.prevent-duplication")
        val whitelist = plugin.configYml.getStrings("effects.multiply_drops.whitelist").map { Items.lookup(it) }

        val fractional = config.has("fractional") && config.getBool("fractional")

        val multiplier = if (config.has("fortune")) {
            fortuneDropAmount(config.getIntFromExpression("fortune", data)).toDouble()
        } else if (config.has("multiplier")) {
            config.getDoubleFromExpression("multiplier", data)
        } else 1.0

        event.addModifier { drop ->
            var matches = true
            if (config.has("on_items")) {
                val items = config.getFormattedStrings("on_items", data).map { string -> Items.lookup(string) }
                matches = items.any { test -> test.matches(drop) }
            }

            if (drop.maxStackSize > 1 && matches) {
                if (isBlacklisting && !event.duplicationSafe && isProtected(drop, whitelist)) {
                    return@addModifier DropResult(drop, 0)
                }

                drop.amount = if (fractional) scaled(drop.amount, multiplier) else drop.amount * wholeMultiplier(multiplier)
            }

            DropResult(drop, 0)
        }

        return true
    }

    private fun isProtected(drop: ItemStack, whitelist: List<TestableItem>): Boolean =
        drop.type.isOccluding && !whitelist.matches(drop)

    private fun wholeMultiplier(multiplier: Double): Int =
        if (multiplier > 0) multiplier.roundToInt().coerceAtLeast(1) else multiplier.roundToInt()

    private fun scaled(amount: Int, multiplier: Double): Int {
        val exact = amount * multiplier
        val whole = floor(exact)
        return whole.toInt() + if (Random.nextDouble() < exact - whole) 1 else 0
    }
}
