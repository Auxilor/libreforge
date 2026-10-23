package com.willfp.libreforge.integrations.model.impl

import com.willfp.eco.core.integrations.model.ModelManager
import com.willfp.libreforge.get
import com.willfp.libreforge.triggers.TriggerData
import org.bukkit.entity.LivingEntity

/** The triggering entity if it has a model, otherwise the victim if it has one. */
internal fun TriggerData.modelTarget(): LivingEntity? =
    listOfNotNull(dispatcher.get<LivingEntity>(), victim)
        .firstOrNull { ModelManager.hasModel(it, null) }
