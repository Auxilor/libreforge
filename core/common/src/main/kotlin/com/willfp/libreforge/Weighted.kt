package com.willfp.libreforge

import com.willfp.libreforge.triggers.TriggerData
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.inventory.ItemStack

interface Weighted {
    val weight: Double
    fun calcWeight():Double
    fun calcWeight(data: TriggerData):Double
}

data class WeightedItems(
    val items: List<ItemStack>,
    override val weight: Double
) : Weighted {
    override fun calcWeight(): Double {
        return weight
    }
    override fun calcWeight(data: TriggerData): Double {
        return weight
    }
}

open class WeightedList<T : Weighted>(
    list: List<T>
) : DelegatedList<T>(list) {
    fun random(data: TriggerData) = this.randomOrNull(data)?: throw NoSuchElementException("List is empty")
    fun random() = this.randomOrNull() ?: throw NoSuchElementException("List is empty")

    fun randomOrNull(): T? {
        if (this.isEmpty()) {
            return null
        }
        val totalWeight = this.sumOf { it.calcWeight() }

        if (totalWeight == 0.0) {
            val randomIndex = (Math.random() * this.size).toInt()
            return this[randomIndex]
        }

        val random = Math.random() * totalWeight
        var current = 0.0
        for (item in this) {
            current += item.calcWeight()
            if (random < current) {
                return item
            }
        }
        return null
    }
    fun randomOrNull(data: TriggerData): T? {
        if (this.isEmpty()) {
            return null
        }
        val totalWeight = this.sumOf { it.calcWeight(data) }

        if (totalWeight == 0.0) {
            val randomIndex = (Math.random() * this.size).toInt()
            return this[randomIndex]
        }

        val random = Math.random() * totalWeight
        var current = 0.0
        for (item in this) {
            current += item.calcWeight(data)
            if (random < current) {
                return item
            }
        }

        return null
    }
}

inline fun <reified T : Weighted> List<T>.toWeightedList(): WeightedList<T> =
    WeightedList(this)
