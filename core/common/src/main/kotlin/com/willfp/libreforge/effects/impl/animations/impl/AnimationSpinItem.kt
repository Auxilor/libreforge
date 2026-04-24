package com.willfp.libreforge.effects.impl.animations.impl

import com.willfp.eco.core.Prerequisite
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.items.Items
import com.willfp.libreforge.*
import com.willfp.libreforge.effects.impl.animations.Animation
import com.willfp.libreforge.triggers.TriggerData
import dev.romainguy.kotlin.math.Float3
import org.bukkit.Location
import org.bukkit.entity.ArmorStand
import org.bukkit.util.EulerAngle
import kotlin.math.cos
import kotlin.math.sin

object AnimationSpinItem : Animation<NoCompileData, List<ArmorStand>>("spin_item") {
    override val arguments = arguments {
        require("item", "You must specify the item!")
        require("amount", "You must specify the amount of items!")
        require("duration", "You must specify the duration!")
        require("radius", "You must specify the radius!")
        require("speed", "You must specify the speed!")
    }

    override fun setUp(
        sourceLocation: Location,
        direction: Float3,
        config: Config,
        data: TriggerData,
        compileData: NoCompileData
    ): List<ArmorStand> {
        val armorStands = mutableListOf<ArmorStand>()
        val item = Items.lookup(config.getString("item")).item

        for (i in 0 until config.getIntFromExpression("amount", data)) {
            val armorStand = sourceLocation.world!!.spawn(sourceLocation, ArmorStand::class.java) {
                it.setGravity(false)
                it.isVisible = false
                it.isInvulnerable = true
                it.setArms(true)
                it.setBasePlate(false)
                it.canPickupItems = false
                it.isCollidable = false
                it.isMarker = true
                it.isSilent = true
                it.removeWhenFarAway = false
                it.equipment.setItemInMainHand(item)
            }

            armorStands.add(armorStand)
        }

        return armorStands
    }

    override fun play(
        tick: Int,
        sourceLocation: Location,
        direction: Float3,
        config: Config,
        triggerData: TriggerData,
        compileData: NoCompileData,
        data: List<ArmorStand>
    ): Boolean {
        val speed = config.getDoubleFromExpression("speed", triggerData)
        val radius = config.getDoubleFromExpression("radius", triggerData)
        val amount = config.getIntFromExpression("amount", triggerData)

        val angle = tick * speed * 10

        data.forEachIndexed { index, armorStand ->
            val armorStandAngle = angle + index * 2 * Math.PI / amount // evenly spaced in radians
            val x = cos(armorStandAngle) * radius
            val z = sin(armorStandAngle) * radius

            val armorStandLocation = sourceLocation.clone().add(x, 0.5, z)
            if (Prerequisite.HAS_PAPER.isMet)
                armorStand.teleportAsync(armorStandLocation)
            else
                armorStand.teleport(armorStandLocation) // damn spigot
            armorStand.rightArmPose =
                EulerAngle(0.0, armorStandAngle + Math.PI, 0.0) // Add PI to make the item point outwards
        }

        return tick >= config.getDoubleFromExpression("duration", triggerData)
    }

    override fun finish(
        sourceLocation: Location,
        direction: Float3,
        config: Config,
        triggerData: TriggerData,
        compileData: NoCompileData,
        data: List<ArmorStand>
    ) {
        data.forEach { plugin.scheduler.runTask(it) { it.remove() } }
    }
}
