package com.willfp.libreforge.triggers

import com.willfp.libreforge.integrations.paper.TriggerSwing
import com.willfp.libreforge.triggers.triggers.TriggerAltClick
import com.willfp.libreforge.triggers.triggers.TriggerBite
import com.willfp.libreforge.triggers.triggers.TriggerBlockItemDrop
import com.willfp.libreforge.triggers.triggers.TriggerBowAttack
import com.willfp.libreforge.triggers.triggers.TriggerBreed
import com.willfp.libreforge.triggers.triggers.TriggerBrew
import com.willfp.libreforge.triggers.triggers.TriggerCastRod
import com.willfp.libreforge.triggers.triggers.TriggerCatchEntity
import com.willfp.libreforge.triggers.triggers.TriggerCatchFish
import com.willfp.libreforge.triggers.triggers.TriggerCatchFishFail
import com.willfp.libreforge.triggers.triggers.TriggerChangeArmor
import com.willfp.libreforge.triggers.triggers.TriggerChangeWorld
import com.willfp.libreforge.triggers.triggers.TriggerConsume
import com.willfp.libreforge.triggers.triggers.TriggerCraft
import com.willfp.libreforge.triggers.triggers.TriggerCustom
import com.willfp.libreforge.triggers.triggers.TriggerDamageItem
import com.willfp.libreforge.triggers.triggers.TriggerDeath
import com.willfp.libreforge.triggers.triggers.TriggerDeployElytra
import com.willfp.libreforge.triggers.triggers.TriggerDropItem
import com.willfp.libreforge.triggers.triggers.TriggerEnchantItem
import com.willfp.libreforge.triggers.triggers.TriggerEntityItemDrop
import com.willfp.libreforge.triggers.triggers.TriggerFallDamage
import com.willfp.libreforge.triggers.triggers.TriggerGainHunger
import com.willfp.libreforge.triggers.triggers.TriggerGainXp
import com.willfp.libreforge.triggers.triggers.TriggerHeadshot
import com.willfp.libreforge.triggers.triggers.TriggerHeal
import com.willfp.libreforge.triggers.triggers.TriggerHoldItem
import com.willfp.libreforge.triggers.triggers.TriggerHookInGround
import com.willfp.libreforge.triggers.triggers.TriggerItemBreak
import com.willfp.libreforge.triggers.triggers.TriggerJoin
import com.willfp.libreforge.triggers.triggers.TriggerJump
import com.willfp.libreforge.triggers.triggers.TriggerKill
import com.willfp.libreforge.triggers.triggers.TriggerLeashEntity
import com.willfp.libreforge.triggers.triggers.TriggerLeave
import com.willfp.libreforge.triggers.triggers.TriggerLevelUpXp
import com.willfp.libreforge.triggers.triggers.TriggerLoseHunger
import com.willfp.libreforge.triggers.triggers.TriggerLosePotionEffect
import com.willfp.libreforge.triggers.triggers.TriggerMeleeAttack
import com.willfp.libreforge.triggers.triggers.TriggerMineBlock
import com.willfp.libreforge.triggers.triggers.TriggerMineBlockProgress
import com.willfp.libreforge.triggers.triggers.TriggerMove
import com.willfp.libreforge.triggers.triggers.TriggerPickUpItem
import com.willfp.libreforge.triggers.triggers.TriggerPlaceBlock
import com.willfp.libreforge.triggers.triggers.TriggerPotionEffect
import com.willfp.libreforge.triggers.triggers.TriggerProjectileHit
import com.willfp.libreforge.triggers.triggers.TriggerProjectileLaunch
import com.willfp.libreforge.triggers.triggers.TriggerReelIn
import com.willfp.libreforge.triggers.triggers.TriggerRespawn
import com.willfp.libreforge.triggers.triggers.TriggerSendMessage
import com.willfp.libreforge.triggers.triggers.TriggerShieldBlock
import com.willfp.libreforge.triggers.triggers.TriggerShootBow
import com.willfp.libreforge.triggers.triggers.TriggerSmelt
import com.willfp.libreforge.triggers.triggers.TriggerStatic
import com.willfp.libreforge.triggers.triggers.TriggerSwapHands
import com.willfp.libreforge.triggers.triggers.TriggerTakeDamage
import com.willfp.libreforge.triggers.triggers.TriggerTakeEntityDamage
import com.willfp.libreforge.triggers.triggers.TriggerToggleFlight
import com.willfp.libreforge.triggers.triggers.TriggerToggleSneak
import com.willfp.libreforge.triggers.triggers.TriggerToggleSprint
import com.willfp.libreforge.triggers.triggers.TriggerTridentAttack
import com.willfp.libreforge.triggers.triggers.TriggerWinRaid

@Suppress("UNUSED")
object Triggers {
    private val BY_ID = mutableMapOf<String, Trigger>()
    private val TRIGGER_GROUPS = mutableMapOf<String, TriggerGroup>()

    val MELEE_ATTACK: Trigger = TriggerMeleeAttack()
    val BOW_ATTACK: Trigger = TriggerBowAttack()
    val TRIDENT_ATTACK: Trigger = TriggerTridentAttack()
    val MINE_BLOCK: Trigger = TriggerMineBlock()
    val JUMP: Trigger = TriggerJump()
    val KILL: Trigger = TriggerKill()
    val PROJECTILE_LAUNCH: Trigger = TriggerProjectileLaunch()
    val TAKE_DAMAGE: Trigger = TriggerTakeDamage()
    val PROJECTILE_HIT: Trigger = TriggerProjectileHit()
    val FALL_DAMAGE: Trigger = TriggerFallDamage()
    val TAKE_ENTITY_DAMAGE: Trigger = TriggerTakeEntityDamage()
    val ALT_CLICK: Trigger = TriggerAltClick()
    val HEAL: Trigger = TriggerHeal()
    val LOSE_HUNGER: Trigger = TriggerLoseHunger()
    val GAIN_XP: Trigger = TriggerGainXp()
    val SHIELD_BLOCK: Trigger = TriggerShieldBlock()
    val BLOCK_ITEM_DROP: Trigger = TriggerBlockItemDrop()
    val ENTITY_ITEM_DROP: Trigger = TriggerEntityItemDrop()
    val GAIN_HUNGER: Trigger = TriggerGainHunger()
    val MOVE: Trigger = TriggerMove()
    val SWAP_HANDS: Trigger = TriggerSwapHands()
    val SWING: Trigger = TriggerSwing()
    val TOGGLE_FLIGHT: Trigger = TriggerToggleFlight()
    val TOGGLE_SNEAK: Trigger = TriggerToggleSneak()
    val TOGGLE_SPRINT: Trigger = TriggerToggleSprint()
    val POTION_EFFECT: Trigger = TriggerPotionEffect()
    val LOSE_POTION_EFFECT: Trigger = TriggerLosePotionEffect()
    val DEATH: Trigger = TriggerDeath()
    val MINE_BLOCK_PROGRESS: Trigger = TriggerMineBlockProgress()
    val ITEM_BREAK: Trigger = TriggerItemBreak()
    val BITE: Trigger = TriggerBite()
    val CAST_ROD: Trigger = TriggerCastRod()
    val CATCH_ENTITY: Trigger = TriggerCatchEntity()
    val CATCH_FISH: Trigger = TriggerCatchFish()
    val CATCH_FISH_FAIL: Trigger = TriggerCatchFishFail()
    val HOOK_IN_GROUND: Trigger = TriggerHookInGround()
    val REEL_IN: Trigger = TriggerReelIn()
    val DEPLOY_ELYTRA: Trigger = TriggerDeployElytra()
    val SHOOT_BOW: Trigger = TriggerShootBow()
    val CONSUME: Trigger = TriggerConsume()
    val CRAFT: Trigger = TriggerCraft()
    val ENCHANT_ITEM: Trigger = TriggerEnchantItem()
    val DAMAGE_ITEM: Trigger = TriggerDamageItem()
    val WIN_RAID: Trigger = TriggerWinRaid()
    val SMELT: Trigger = TriggerSmelt()
    val BREW: Trigger = TriggerBrew()
    val PLACE_BLOCK: Trigger = TriggerPlaceBlock()
    val BREED: Trigger = TriggerBreed()
    val HEADSHOT: Trigger = TriggerHeadshot()
    val JOIN: Trigger = TriggerJoin()
    val LEAVE: Trigger = TriggerLeave()
    val LEVEL_UP_XP: Trigger = TriggerLevelUpXp()
    val CHANGE_WORLD: Trigger = TriggerChangeWorld()
    val RESPAWN: Trigger = TriggerRespawn()
    val HOLD_ITEM: Trigger = TriggerHoldItem()
    val SEND_MESSAGE: Trigger = TriggerSendMessage()
    val CHANGE_ARMOR: Trigger = TriggerChangeArmor()
    val PICK_UP_ITEM: Trigger = TriggerPickUpItem()
    val DROP_ITEM: Trigger = TriggerDropItem()
    val LEASH_ENTITY: Trigger = TriggerLeashEntity()


    fun values(): Set<Trigger> {
        return BY_ID.values.toSet()
    }

    fun all(): () -> Set<Trigger> {
        return { values() }
    }

    fun withParameters(vararg parameters: TriggerParameter): () -> Set<Trigger> {
        return {
            val found = mutableSetOf<Trigger>()

            for (trigger in values()) {
                if (trigger.parameters.containsAll(parameters.toSet())) {
                    found.add(trigger)
                }
            }

            found
        }
    }

    fun getById(id: String): Trigger? {
        for ((prefix, group) in TRIGGER_GROUPS) {
            if (id.startsWith("${prefix}_")) {
                return group.create(id.removePrefix("${prefix}_"))
            }
        }

        return BY_ID[id.lowercase()]
    }

    /**
     * Add new trigger.
     *
     * @param trigger The trigger to add.
     */
    fun addNewTrigger(trigger: Trigger) {
        BY_ID.remove(trigger.id)
        BY_ID[trigger.id] = trigger
    }

    /**
     * Add new trigger group.
     *
     * @param group The trigger group to add.
     */
    fun addNewTriggerGroup(group: TriggerGroup) {
        TRIGGER_GROUPS.remove(group.prefix)
        TRIGGER_GROUPS[group.prefix] = group
    }

    init {
        TriggerStatic.registerGroup()
        TriggerCustom.registerGroup()
    }
}
