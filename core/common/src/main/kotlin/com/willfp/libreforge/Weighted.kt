package com.willfp.libreforge

import org.bukkit.inventory.ItemStack

interface Weighted {
    val weight: Double
}

data class WeightedItems(
    val items: List<ItemStack>,
    override val weight: Double
) : Weighted

open class WeightedList<T : Weighted>(
    list: List<T>
) : DelegatedList<T>(list) {
    fun random() = this.randomOrNull() ?: throw NoSuchElementException("List is empty")

    fun random(weightSelector: (T) -> Double) =
        this.randomOrNull(weightSelector) ?: throw NoSuchElementException("List is empty")

    fun randomOrNull(): T? = this.randomOrNull { it.weight }

    fun randomOrNull(weightSelector: (T) -> Double): T? {
        if (this.isEmpty()) {
            return null
        }

        val totalWeight = this.sumOf { weightSelector(it).coerceAtLeast(0.0) }
        if (totalWeight == 0.0) {
            val randomIndex = (Math.random() * this.size).toInt()
            return this[randomIndex]
        }

        val random = Math.random() * totalWeight
        var current = 0.0
        for (item in this) {
            current += weightSelector(item).coerceAtLeast(0.0)
            if (random < current) {
                return item
            }
        }

        return this.lastOrNull { weightSelector(it) > 0.0 }
    }
}

inline fun <reified T : Weighted> List<T>.toWeightedList(): WeightedList<T> =
    WeightedList(this)

