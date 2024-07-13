package com.willfp.libreforge.conditions.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.get
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player

object ConditionIsSubmerged: Condition<NoCompileData>("is_submerged") {

    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ): Boolean {
        val livingEntity = dispatcher.get<LivingEntity>() ?: return false

        val box = livingEntity.boundingBox
        val max = box.max.toLocation(livingEntity.world)
        val min = box.min.toLocation(livingEntity.world)
        val center = box.center.toLocation(livingEntity.world)

        val areLocationsBothLiquid = listOf(livingEntity.location, livingEntity.eyeLocation, max, min, center).all { location ->
            val locationMaterial = location.block.type
            locationMaterial == Material.WATER || locationMaterial == Material.LAVA
        }
        return areLocationsBothLiquid
    }

}