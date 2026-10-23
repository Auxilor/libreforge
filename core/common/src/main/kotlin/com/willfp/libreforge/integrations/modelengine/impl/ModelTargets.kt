package com.willfp.libreforge.integrations.modelengine.impl

import com.willfp.eco.core.integrations.modelengine.BridgedAnimationHandler
import com.willfp.eco.core.integrations.modelengine.ModelEngineManager
import com.willfp.libreforge.get
import com.willfp.libreforge.triggers.TriggerData
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity

/** The triggering entity if it has a model, otherwise the victim if it has one. */
internal fun TriggerData.modelTarget(): LivingEntity? =
    listOfNotNull(dispatcher.get<LivingEntity>(), victim)
        .firstOrNull { ModelEngineManager.getModeledEntity(it) != null }

/** The animation handlers of [entity]'s models, or only of the model with [modelId] when it is set. */
internal fun animationHandlers(entity: Entity, modelId: String?): List<BridgedAnimationHandler> =
    ModelEngineManager.getModeledEntity(entity)?.models.orEmpty()
        .filterKeys { modelId == null || it.equals(modelId, ignoreCase = true) }
        .values
        .map { it.animationHandler }
