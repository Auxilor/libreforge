package com.willfp.libreforge.integrations.luckperms

import net.luckperms.api.event.LuckPermsEvent
import net.luckperms.api.event.group.GroupCreateEvent
import net.luckperms.api.event.group.GroupDataRecalculateEvent
import net.luckperms.api.event.group.GroupDeleteEvent
import net.luckperms.api.event.node.NodeAddEvent
import net.luckperms.api.event.node.NodeMutateEvent
import net.luckperms.api.event.node.NodeRemoveEvent
import net.luckperms.api.event.track.mutate.TrackAddGroupEvent
import net.luckperms.api.event.track.mutate.TrackMutateEvent
import net.luckperms.api.event.track.mutate.TrackRemoveGroupEvent
import net.luckperms.api.event.type.Sourced
import net.luckperms.api.event.user.track.UserTrackEvent
import net.luckperms.api.node.Node
import net.luckperms.api.node.NodeType

/*

Shared readers for LuckPerms events, used by the filters to pull values out of whichever
event happens to be carried by the trigger.

 */

/**
 * The node an event added or removed, if there is one.
 */
internal val LuckPermsEvent.mutatedNode: Node?
    get() = when (this) {
        is NodeAddEvent -> this.node
        is NodeRemoveEvent -> this.node
        else -> null
    }

/**
 * The group name an event relates to, if there is one.
 */
internal val LuckPermsEvent.groupName: String?
    get() = when (this) {
        is UserTrackEvent -> this.groupTo.orElse(this.groupFrom.orElse(null))
        is GroupCreateEvent -> this.group.name
        is GroupDeleteEvent -> this.groupName
        is GroupDataRecalculateEvent -> this.group.name
        is TrackAddGroupEvent -> this.group
        is TrackRemoveGroupEvent -> this.group
        else -> this.mutatedNode?.let { node ->
            NodeType.INHERITANCE.tryCast(node).map { it.groupName }.orElse(null)
        }
    }

/**
 * The track name an event relates to, if there is one.
 */
internal val LuckPermsEvent.trackName: String?
    get() = when (this) {
        is UserTrackEvent -> this.track.name
        is TrackMutateEvent -> this.track.name
        else -> null
    }

/**
 * The source type of an event, if it has one.
 */
internal val LuckPermsEvent.sourceTypeName: String?
    get() = (this as? Sourced)?.source?.type?.name

/**
 * The data type an event mutated, if it mutated one.
 */
internal val LuckPermsEvent.dataTypeName: String?
    get() = (this as? NodeMutateEvent)?.dataType?.name
