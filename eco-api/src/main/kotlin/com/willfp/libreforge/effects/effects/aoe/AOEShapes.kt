package com.willfp.libreforge.effects.effects.aoe

import com.google.common.collect.HashBiMap
import com.google.common.collect.ImmutableList

@Suppress("UNUSED")
object AOEShapes {
    private val BY_ID = HashBiMap.create<String, AOEShape>()

    private val CONE: AOEShape = AOEShapeCone
    private val CIRCLE: AOEShape = AOEShapeCircle
    private val OFFSET_CIRCLE: AOEShape = AOEShapeOffsetCircle
    private val SCAN_IN_FRONT: AOEShape = AOEShapeScanInFront

    /**
     * Get AOEShape matching id.
     *
     * @param id The id to query.
     * @return The matching AOEShape, or null if not found.
     */
    fun getByID(id: String): AOEShape? {
        return BY_ID[id]
    }

    /**
     * List of all registered AOEShapes.
     *
     * @return The conditions.
     */
    fun values(): List<AOEShape> {
        return ImmutableList.copyOf(BY_ID.values)
    }

    /**
     * Add new AOEShapes.
     *
     * @param shape The AOEShape to add.
     */
    fun addNewShape(shape: AOEShape) {
        BY_ID.remove(shape.id)
        BY_ID[shape.id] = shape
    }
}
