package com.willfp.libreforge

import org.bukkit.inventory.ItemStack

interface Weighted<T> {
    val obj: T
    val weight: Double

    fun get(): T = obj
}

data class WeightedItems(
    override val obj: List<ItemStack>,
    override val weight: Double
) : Weighted<List<ItemStack>>

class WeightedList<T : Weighted<*>>(
    list: List<T>
) : DelegatedList<T>(list) {
    fun randomOrNull(): T? {
        if (this.isEmpty()) {
            return null
        }

        val totalWeight = this.sumOf { it.weight }
        val random = Math.random() * totalWeight

        var current = 0.0
        for (item in this) {
            current += item.weight
            if (random < current) {
                return item
            }
        }

        return null
    }
}

inline fun <reified T : Weighted<*>> List<T>.toWeightedList(): WeightedList<T> =
    WeightedList(this)

