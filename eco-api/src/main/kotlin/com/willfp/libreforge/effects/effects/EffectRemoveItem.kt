package com.willfp.libreforge.effects.effects

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.items.Items
import com.willfp.eco.core.recipe.parts.EmptyTestableItem
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.Triggers
import org.bukkit.Material
import kotlin.math.max
import kotlin.math.min

class EffectRemoveItem: Effect(
    "remove_item",
    triggers = Triggers.withParameters(TriggerParameter.PLAYER)
) {
    override fun handle(data: TriggerData, config: Config) {
        val player = data.player ?: return

        val test = Items.lookup(config.getString("item"))
        val amount = test.item.amount

        if (test is EmptyTestableItem) {
            return
        }

        var toRemove = amount

        for (itemStack in player.inventory) {
            if (test.matches(itemStack)) {
                val removed = min(toRemove, itemStack.amount)

                if (removed == itemStack.amount) {
                    itemStack.type = Material.AIR
                }

                itemStack.amount -= removed
                toRemove -= removed

                if (toRemove <= 0) {
                    return
                }
            }
        }
    }

    override fun validateConfig(config: Config): List<ConfigViolation> {
        val violations = mutableListOf<ConfigViolation>()

        if (!config.has("item")) violations.add(
            ConfigViolation(
                "item",
                "You must specify an item lookup string to remove! " +
                        "https://plugins.auxilor.io/all-plugins/the-item-lookup-system"
            )
        )

        return violations
    }
}
