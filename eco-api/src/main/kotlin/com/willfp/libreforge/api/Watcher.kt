package com.willfp.libreforge.api

import org.bukkit.entity.Player
import org.bukkit.event.block.BlockBreakEvent
import com.willfp.eco.core.config.interfaces.JSONConfig
import org.bukkit.entity.LivingEntity
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.entity.Arrow
import org.bukkit.entity.Trident
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.entity.Projectile
import org.bukkit.event.entity.ProjectileLaunchEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.ProjectileHitEvent
import com.willfp.eco.core.events.EntityDeathByEntityEvent
import org.bukkit.block.Block
import org.bukkit.event.player.PlayerItemDamageEvent

interface Watcher {
    /**
     * Called when a player breaks a block.
     *
     * @param player The player.
     * @param block  The block that was broken.
     * @param event  The event that called this watcher.
     * @param config The effect config.
     */
    fun onBlockBreak(
        player: Player,
        block: Block,
        event: BlockBreakEvent,
        config: JSONConfig
    ) {
        // Empty default as effects only override required watchers.
    }

    /**
     * Called when an entity attacks another entity via any mean.
     *
     * @param attacker The attacker.
     * @param victim   The victim.
     * @param event    The event.
     * @param config   The effect config.
     */
    fun onAnyDamage(
        attacker: Player,
        victim: LivingEntity,
        event: EntityDamageByEntityEvent,
        config: JSONConfig
    ) {
        // Empty default as effects only override required watchers.
    }

    /**
     * Called when an entity shoots another entity with an arrow.
     *
     * @param attacker The shooter.
     * @param victim   The victim.
     * @param arrow    The arrow entity.
     * @param event    The event that called this watcher.
     * @param config   The effect config.
     */
    fun onArrowDamage(
        attacker: Player,
        victim: LivingEntity,
        arrow: Arrow,
        event: EntityDamageByEntityEvent,
        config: JSONConfig
    ) {
        // Empty default as effects only override required watchers.
    }

    /**
     * Called when an entity damages another entity with a trident throw.
     *
     * @param attacker The shooter.
     * @param victim   The victim.
     * @param trident  The trident entity.
     * @param event    The event that called this watcher.
     * @param config   The effect config.
     */
    fun onTridentDamage(
        attacker: Player,
        victim: LivingEntity,
        trident: Trident,
        event: EntityDamageByEntityEvent,
        config: JSONConfig
    ) {
        // Empty default as effects only override required watchers.
    }

    /**
     * Called when a player jumps.
     *
     * @param player The player.
     * @param event  The event that called this watcher.
     * @param config The effect config.
     */
    fun onJump(
        player: Player,
        event: PlayerMoveEvent,
        config: JSONConfig
    ) {
        // Empty default as effects only override required watchers.
    }

    /**
     * Called when an entity attacks another entity with a melee attack.
     *
     * @param attacker The attacker.
     * @param victim   The victim.
     * @param event    The event that called this watcher.
     * @param config   The effect config.
     */
    fun onMeleeAttack(
        attacker: Player,
        victim: LivingEntity,
        event: EntityDamageByEntityEvent,
        config: JSONConfig
    ) {
        // Empty default as effects only override required watchers.
    }

    /**
     * Called when an entity shoots a projectile.
     *
     * @param shooter    The entity that shot the bow.
     * @param projectile The projectile that was shot.
     * @param event      The event that called this watcher.
     * @param config     The effect config.
     */
    fun onProjectileLaunch(
        shooter: Player,
        projectile: Projectile,
        event: ProjectileLaunchEvent,
        config: JSONConfig
    ) {
        // Empty default as effects only override required watchers.
    }

    /**
     * Called when an entity takes fall damage.
     *
     * @param faller The entity that took the fall damage.
     * @param event  The event that called this watcher.
     * @param config The effect config.
     */
    fun onFallDamage(
        faller: Player,
        event: EntityDamageEvent,
        config: JSONConfig
    ) {
        // Empty default as effects only override required watchers.
    }

    /**
     * Called when a projectile hits a block or entity.
     *
     * @param shooter The entity that shot the arrow.
     * @param event   The event that called this watcher.
     * @param config  The effect config.
     */
    fun onProjectileHit(
        shooter: Player,
        event: ProjectileHitEvent,
        config: JSONConfig
    ) {
        // Empty default as effects only override required watchers.
    }

    /**
     * Called when an entity kills another entity.
     *
     * @param killer The killer.
     * @param victim The victim.
     * @param event  The event.
     * @param config The effect config.
     */
    fun onKill(
        killer: Player,
        victim: LivingEntity,
        event: EntityDeathByEntityEvent,
        config: JSONConfig
    ) {
        // Empty default as effects only override required watchers.
    }

    /**
     * Called when an entity takes damage wearing armor.
     *
     * @param victim The entity that took damage.
     * @param event  The event that called this watcher.
     * @param config The effect config.
     */
    fun onDamageWearingArmor(
        victim: Player,
        event: EntityDamageEvent,
        config: JSONConfig
    ) {
        // Empty default as effects only override required watchers.
    }

    /**
     * Called when an entity throws a trident.
     *
     * @param shooter The entity that threw the trident.
     * @param trident The trident that was thrown.
     * @param event   The event that called this watcher.
     * @param config  The effect config.
     */
    fun onTridentLaunch(
        shooter: Player,
        trident: Trident,
        event: ProjectileLaunchEvent,
        config: JSONConfig
    ) {
        // Empty default as effects only override required watchers.
    }

    /**
     * Called when an item takes durability damage.
     *
     * @param event  The event that called this watcher.
     * @param config The effect config.
     */
    fun onDurabilityDamage(
        event: PlayerItemDamageEvent,
        config: JSONConfig
    ) {
        // Empty default as effects only override required watchers.
    }
}