package com.willfp.libreforge

import com.willfp.eco.util.NamespacedKeyUtils
import com.willfp.eco.util.NumberUtils
import com.willfp.libreforge.conditions.ConditionList
import com.willfp.libreforge.effects.EffectList
import org.bukkit.NamespacedKey
import java.util.Objects

/**
 * A holder 'holds' a list of effects and a list of conditions.
 * This is essentially the core of libreforge, and implementations
 * are things like Talismans, Enchantment Levels, Items, etc.
 */
interface Holder {
    /**
     * The ID of the holder, must be unique.
     */
    val id: NamespacedKey

    /**
     * The effects.
     */
    val effects: EffectList

    /**
     * The conditions.
     */
    val conditions: ConditionList
}

/**
 * A template that a may create a holder when given an ID.
 */
data class HolderTemplate(
    val effects: EffectList,
    val conditions: ConditionList
) {
    /**
     * Create a holder from the template.
     */
    fun toHolder() = toHolder(
        NamespacedKeyUtils.createEcoKey("template_${NumberUtils.randInt(0, 1000000)}")
    )

    /**
     * Create a holder with a given key from the template.
     */
    fun toHolder(key: NamespacedKey): Holder = HolderFromTemplate(
        effects,
        conditions,
        key
    )

    private class HolderFromTemplate(
        override val effects: EffectList,
        override val conditions: ConditionList,
        override val id: NamespacedKey
    ) : Holder {
        override fun equals(other: Any?): Boolean {
            if (other !is Holder) {
                return false
            }

            return other.id == this.id
        }

        override fun hashCode(): Int {
            return Objects.hash(this.id)
        }
    }
}
