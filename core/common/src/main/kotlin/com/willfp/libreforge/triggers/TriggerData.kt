package com.willfp.libreforge.triggers

import com.willfp.eco.core.items.HashedItem
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.EmptyProvidedHolder
import com.willfp.libreforge.GlobalDispatcher
import com.willfp.libreforge.NamedValue
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.getProvider
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.impl.TriggerBlank
import org.bukkit.Location
import org.bukkit.block.Block
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.Event
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector
import java.util.Objects

enum class TriggerParameter(
    vararg val inheritsFrom: TriggerParameter
) {
    PLAYER,
    VICTIM,
    BLOCK,
    EVENT,
    LOCATION(VICTIM, PLAYER),
    PROJECTILE,
    VELOCITY(PLAYER, VICTIM),
    ITEM(PLAYER, VICTIM),
    TEXT,
    VALUE,
    ALT_VALUE
}

class TriggerData(
    val dispatcher: Dispatcher<*> = GlobalDispatcher,
    val player: Player? = null,
    val victim: LivingEntity? = null,
    val block: Block? = null,
    val blockData: BlockData? = null, //fixes checks with multiply drops effect (ageable)
    val event: Event? = null,
    val location: Location? = victim?.location ?: player?.location,
    val projectile: Projectile? = null,
    val velocity: Vector? = player?.velocity ?: victim?.velocity,
    val item: ItemStack? = player?.inventory?.itemInMainHand ?: victim?.equipment?.itemInMainHand,
    val text: String? = null,
    val value: Double = 1.0,
    val altValue: Double = 1.0
) {
    // The holders and dispatchers are automatically added when triggers are dispatched,
    // so they are not included in the constructor.
    var holder: ProvidedHolder = EmptyProvidedHolder
        internal set

    /*
    This is a bodge inherited from v3, but it's the only real way to do this.
    Essentially, the player can get messed up by mutators, and that causes
    placeholders to parse incorrectly when doing Config#get<x>FromExpression.

    It's really not very nice, but it's good enough. Just don't think about it.
     */
    internal var originalPlayer: Player? = player

    /*
    This is a patch that allows for more fluent TriggerData -> DispatchedTrigger
    conversions. Essentially, it passes a reference to the original trigger's
    placeholders so that when a DispatchedTrigger is created from a TriggerData,
    it can inherit the placeholders from the original trigger.
     */
    internal var inheritedTriggerPlaceholders: Collection<NamedValue>? = null

    /**
     * Shorthand to get the item (if any) that is attached to the trigger / holder.
     */
    val foundItem: ItemStack?
        get() = holder.getProvider() ?: item

    private val hashCode by lazy {
        Objects.hash(
            holder,
            player,
            victim,
            block,
            event,
            location,
            projectile,
            velocity,
            item?.let { HashedItem.of(it) },
            text,
            value,
            altValue
        )
    }

    /**
     * Turn into a dispatched trigger for a [player].
     */
    @Deprecated(
        "Use dispatch(dispatcher) instead",
        ReplaceWith("dispatch(player.toDispatcher())"),
        DeprecationLevel.ERROR
    )
    fun dispatch(player: Player) =
        dispatch(player.toDispatcher())

    /**
     * Turn into a dispatched trigger for a new [dispatcher].
     */
    fun dispatch(dispatcher: Dispatcher<*>) = DispatchedTrigger(
        dispatcher,
        TriggerBlank,
        this
    ).apply {
        inheritedTriggerPlaceholders?.let {
            addPlaceholders(it)
        }
    }

    /**
     * Copy this [TriggerData], optionally overriding some values.
     */
    fun copy(
        dispatcher: Dispatcher<*> = this.dispatcher,
        player: Player? = this.player,
        victim: LivingEntity? = this.victim,
        block: Block? = this.block,
        blockData:BlockData? = this.blockData,
        event: Event? = this.event,
        location: Location? = this.location,
        projectile: Projectile? = this.projectile,
        velocity: Vector? = this.velocity,
        item: ItemStack? = this.item,
        text: String? = this.text,
        value: Double = this.value,
        altValue: Double = this.altValue
    ): TriggerData {
        val copy = TriggerData(
            dispatcher,
            player,
            victim,
            block,
            blockData,
            event,
            location,
            projectile,
            velocity,
            item,
            text,
            value,
            altValue
        )

        copy.holder = this.holder
        copy.originalPlayer = this.originalPlayer
        copy.inheritedTriggerPlaceholders = this.inheritedTriggerPlaceholders

        return copy
    }

    override fun hashCode(): Int {
        return hashCode
    }

    override fun equals(other: Any?): Boolean {
        return other is TriggerData && other.hashCode() == this.hashCode()
    }

    /*
    Everything below this line is *horrible*, but it's the only way to make trigger data
    work nicely with previous versions, when TriggerData was a data class.

    All of this stuff is mangling with how kotlin compiles data classes for the JVM,
    and it's really not pretty.

    DO NOT UNDER ANY CIRCUMSTANCES change or remove this code in any way!
     */

    @Suppress("UNUSED_PARAMETER")
    @Deprecated(
        "This is internal! Do not use!",
        ReplaceWith("TriggerData()"),
        DeprecationLevel.ERROR
    )
    constructor(
        holder: ProvidedHolder?,
        player: Player?,
        victim: LivingEntity?,
        block: Block?,
        blockData: BlockData?,
        event: Event?,
        location: Location?,
        projectile: Projectile?,
        velocity: Vector?,
        item: ItemStack?,
        text: String?,
        value: Double,
        altValue: Double,
        originalPlayer: Player?,
        internal1: Int,
        internal2: kotlin.jvm.internal.DefaultConstructorMarker?
    ) : this(
        GlobalDispatcher,
        player,
        victim,
        block,
        blockData,
        event,
        location,
        projectile,
        velocity,
        item,
        text,
        value,
        altValue
    ) {
        this.holder = holder ?: EmptyProvidedHolder
        this.originalPlayer = originalPlayer
    }

    @Suppress("UNUSED_PARAMETER")
    @Deprecated(
        "This is internal! Do not use!",
        ReplaceWith("TriggerData()"),
        DeprecationLevel.ERROR
    )
    constructor(
        holder: ProvidedHolder?,
        dispatcher: Dispatcher<*>?,
        player: Player?,
        victim: LivingEntity?,
        block: Block?,
        blockData: BlockData?,
        event: Event?,
        location: Location?,
        projectile: Projectile?,
        velocity: Vector?,
        item: ItemStack?,
        text: String?,
        value: Double,
        altValue: Double,
        originalPlayer: Player?,
        internal1: Int,
        internal2: kotlin.jvm.internal.DefaultConstructorMarker?
    ) : this(
        dispatcher ?: GlobalDispatcher,
        player,
        victim,
        block,
        blockData,
        event,
        location,
        projectile,
        velocity,
        item,
        text,
        value
    ) {
        this.holder = holder ?: EmptyProvidedHolder
        this.originalPlayer = originalPlayer
    }
    @Suppress("UNUSED_PARAMETER")
    @Deprecated(
        "This is internal! Do not use!",
        ReplaceWith("TriggerData()"),
        DeprecationLevel.ERROR
    )
    constructor(
        holder: ProvidedHolder?,
        player: Player?,
        victim: LivingEntity?,
        block: Block?,
        event: Event?,
        location: Location?,
        projectile: Projectile?,
        velocity: Vector?,
        item: ItemStack?,
        text: String?,
        value: Double,
        originalPlayer: Player?,
        internal1: Int,
        internal2: kotlin.jvm.internal.DefaultConstructorMarker?
    ) : this(
        GlobalDispatcher,
        player,
        victim,
        block,
        null,
        event,
        location,
        projectile,
        velocity,
        item,
        text,
        value,
        altValue
    ) {
        this.holder = holder ?: EmptyProvidedHolder
        this.originalPlayer = originalPlayer
    }

    @Suppress("UNUSED_PARAMETER")
    @Deprecated(
        "This is internal! Do not use!",
        ReplaceWith("TriggerData()"),
        DeprecationLevel.ERROR
    )
    constructor(
        holder: ProvidedHolder?,
        dispatcher: Dispatcher<*>?,
        player: Player?,
        victim: LivingEntity?,
        block: Block?,
        event: Event?,
        location: Location?,
        projectile: Projectile?,
        velocity: Vector?,
        item: ItemStack?,
        text: String?,
        value: Double,
        originalPlayer: Player?,
        internal1: Int,
        internal2: kotlin.jvm.internal.DefaultConstructorMarker?
    ) : this(
        dispatcher ?: GlobalDispatcher,
        player,
        victim,
        block,
        null,
        event,
        location,
        projectile,
        velocity,
        item,
        text,
        value
    ) {
        this.holder = holder ?: EmptyProvidedHolder
        this.originalPlayer = originalPlayer
    }

    @Suppress("UNUSED_PARAMETER")
    constructor(
        dispatcher: Dispatcher<*>?,
        player: Player?,
        victim: LivingEntity?,
        block: Block?,
        event: Event?,
        location: Location?,
        projectile: Projectile?,
        velocity: Vector?,
        item: ItemStack?,
        text: String?,
        value: Double,
        @Suppress("UNUSED_PARAMETER") ignored: Int,
        @Suppress("UNUSED_PARAMETER") marker: kotlin.jvm.internal.DefaultConstructorMarker?
    ) : this(
        dispatcher ?: GlobalDispatcher,
        player,
        victim,
        block,
        null, // blockData
        event,
        location,
        projectile,
        velocity,
        item,
        text,
        value
    )
}
