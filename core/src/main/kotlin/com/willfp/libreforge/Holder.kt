package com.willfp.libreforge

import com.willfp.eco.util.NamespacedKeyUtils
import com.willfp.libreforge.conditions.ConditionList
import com.willfp.libreforge.effects.EffectList
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.Objects
import java.util.concurrent.atomic.AtomicInteger

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
 * A simple holder, just an ID, effects, and conditions.
 */
class SimpleHolder(
    override val id: NamespacedKey,
    override val effects: EffectList,
    override val conditions: ConditionList
) : Holder {
    override fun hashCode(): Int {
        return Objects.hash(id)
    }

    override fun equals(other: Any?): Boolean {
        if (other !is SimpleHolder) {
            return false
        }

        return other.id == this.id
    }
}

/**
 * A blank holder is a holder with no effects or conditions.
 *
 * It's used in triggers in order to be able to provide an
 * empty provided holder so the holders can then be attached
 * to a copy of the data before it's processed by effects.
 */
object BlankHolder : Holder {
    override val id = plugin.namespacedKeyFactory.create("blank")

    override val effects = EffectList(emptyList())
    override val conditions = ConditionList(emptyList())
}

/**
 * A provided holder is a holder with the item that has provided it,
 * i.e. The physical ItemStack that has the enchantment on it.
 */
interface ProvidedHolder {
    /**
     * The holder.
     */
    val holder: Holder

    /**
     * The provider.
     */
    val provider: Any?

    // Destructuring support
    operator fun component1() = holder
    operator fun component2() = provider

    /**
     * Get not met lines for a [dispatcher].
     */
    fun getNotMetLines(dispatcher: Dispatcher<*>): List<String> {
        val lines = mutableListOf<String>()

        lines += holder.conditions.getNotMetLines(dispatcher, this)
        lines += holder.effects.map { it.conditions }.flatMap { it.getNotMetLines(dispatcher, this) }

        return lines
    }

    /**
     * Get not met lines for a [player].
     */
    fun getNotMetLines(player: Player): List<String> =
        getNotMetLines(player.toDispatcher())

    /**
     * Get if the holder is showing any not met lines for a [dispatcher], or if any
     * conditions are not met and have showNotMet set to true (e.g. for Enchantment Strikethrough).
     */
    fun isShowingAnyNotMet(dispatcher: Dispatcher<*>): Boolean {
        return holder.conditions.isShowingAnyNotMet(dispatcher, this)
                || holder.effects.any { it.conditions.isShowingAnyNotMet(dispatcher, this) }
    }

    /**
     * Get if the holder is showing any not met lines for a [player], or if any
     * conditions are not met and have showNotMet set to true (e.g. for Enchantment Strikethrough).
     */
    fun isShowingAnyNotMet(player: Player): Boolean =
        isShowingAnyNotMet(player.toDispatcher())
}

/**
 * Get the provider cleanly, without casting.
 */
inline fun <reified T> ProvidedHolder.getProvider(): T? {
    return this.provider as? T
}

/**
 * An empty provided holder is a provided holder with no item.
 *
 * Used internally to provide a default value for TriggerData.
 */
object EmptyProvidedHolder : ProvidedHolder {
    override val holder = BlankHolder
    override val provider = null
}

/**
 * Provided holder for nothing.
 */
class SimpleProvidedHolder(
    override val holder: Holder
) : ProvidedHolder {
    override val provider = null

    override fun hashCode(): Int {
        return Objects.hash(holder)
    }

    override fun equals(other: Any?): Boolean {
        if (other !is SimpleProvidedHolder) {
            return false
        }

        return other.holder == this.holder
    }
}

/**
 * A provided holder for an ItemStack.
 */
class ItemProvidedHolder(
    override val holder: Holder,
    override val provider: ItemStack
) : ProvidedHolder {
    override fun hashCode(): Int {
        return Objects.hash(holder, provider)
    }

    override fun equals(other: Any?): Boolean {
        if (other !is ItemProvidedHolder) {
            return false
        }

        return other.holder == this.holder
                && other.provider == this.provider
    }
}

// The current template ID, incremented every time a holder is created from a template.
private val currentTemplateID = AtomicInteger(0)

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
        NamespacedKeyUtils.createEcoKey("template_${currentTemplateID.addAndGet(1)}")
    )

    /**
     * Create a holder with a given key from the template.
     */
    fun toHolder(key: NamespacedKey): Holder = SimpleHolder(
        key,
        effects,
        conditions
    )
}

/**
 * A nested holder has a parent, to allow passing through placeholders.
 */
class NestedHolder(
    private val holder: Holder,
    val parent: ProvidedHolder
) : Holder by holder {
    companion object {
        init {
            registerHolderPlaceholderProvider<NestedHolder> { h, dispatcher ->
                h.parent.generatePlaceholders(dispatcher)
            }
        }
    }
}

fun Holder.nest(parent: ProvidedHolder) = NestedHolder(this, parent)
