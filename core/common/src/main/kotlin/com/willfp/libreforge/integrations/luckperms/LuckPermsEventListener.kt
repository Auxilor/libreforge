package com.willfp.libreforge.integrations.luckperms

import com.willfp.eco.core.EcoPlugin
import com.willfp.libreforge.GlobalDispatcher
import com.willfp.libreforge.integrations.luckperms.impl.TriggerLpConfigReload
import com.willfp.libreforge.integrations.luckperms.impl.TriggerLpContextUpdate
import com.willfp.libreforge.integrations.luckperms.impl.TriggerLpCustomMessage
import com.willfp.libreforge.integrations.luckperms.impl.TriggerLpDemote
import com.willfp.libreforge.integrations.luckperms.impl.TriggerLpFirstLogin
import com.willfp.libreforge.integrations.luckperms.impl.TriggerLpGroupAdd
import com.willfp.libreforge.integrations.luckperms.impl.TriggerLpGroupCreate
import com.willfp.libreforge.integrations.luckperms.impl.TriggerLpGroupDelete
import com.willfp.libreforge.integrations.luckperms.impl.TriggerLpGroupRemove
import com.willfp.libreforge.integrations.luckperms.impl.TriggerLpLogNotify
import com.willfp.libreforge.integrations.luckperms.impl.TriggerLpMetaChange
import com.willfp.libreforge.integrations.luckperms.impl.TriggerLpNodeAdd
import com.willfp.libreforge.integrations.luckperms.impl.TriggerLpNodeClear
import com.willfp.libreforge.integrations.luckperms.impl.TriggerLpNodeRemove
import com.willfp.libreforge.integrations.luckperms.impl.TriggerLpPermissionAdd
import com.willfp.libreforge.integrations.luckperms.impl.TriggerLpPermissionRemove
import com.willfp.libreforge.integrations.luckperms.impl.TriggerLpPostSync
import com.willfp.libreforge.integrations.luckperms.impl.TriggerLpPrefixChange
import com.willfp.libreforge.integrations.luckperms.impl.TriggerLpPromote
import com.willfp.libreforge.integrations.luckperms.impl.TriggerLpSuffixChange
import com.willfp.libreforge.integrations.luckperms.impl.TriggerLpTrackAddGroup
import com.willfp.libreforge.integrations.luckperms.impl.TriggerLpTrackRemoveGroup
import com.willfp.libreforge.integrations.luckperms.impl.TriggerLpUserLoad
import com.willfp.libreforge.plugin
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.updateEffects
import net.luckperms.api.LuckPerms
import net.luckperms.api.event.LuckPermsEvent
import net.luckperms.api.event.context.ContextUpdateEvent
import net.luckperms.api.event.group.GroupCreateEvent
import net.luckperms.api.event.group.GroupDeleteEvent
import net.luckperms.api.event.log.LogNotifyEvent
import net.luckperms.api.event.messaging.CustomMessageReceiveEvent
import net.luckperms.api.event.node.NodeAddEvent
import net.luckperms.api.event.node.NodeClearEvent
import net.luckperms.api.event.node.NodeMutateEvent
import net.luckperms.api.event.node.NodeRemoveEvent
import net.luckperms.api.event.sync.ConfigReloadEvent
import net.luckperms.api.event.sync.PostSyncEvent
import net.luckperms.api.event.track.mutate.TrackAddGroupEvent
import net.luckperms.api.event.track.mutate.TrackRemoveGroupEvent
import net.luckperms.api.event.user.UserDataRecalculateEvent
import net.luckperms.api.event.user.UserFirstLoginEvent
import net.luckperms.api.event.user.UserLoadEvent
import net.luckperms.api.event.user.track.UserDemoteEvent
import net.luckperms.api.event.user.track.UserPromoteEvent
import net.luckperms.api.model.user.User
import net.luckperms.api.node.Node
import net.luckperms.api.node.NodeType
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.UUID

/**
 * LuckPerms events are dispatched through its own event bus rather than through Bukkit,
 * so they cannot be listened to with @EventHandler on the triggers themselves. Instead,
 * everything is subscribed to here and fanned out to the triggers.
 *
 * The LuckPerms event bus fires asynchronously, so every dispatch is passed to the main
 * thread first, as libreforge effects are not thread-safe.
 */
internal object LuckPermsEventListener {
    /**
     * How long to wait for a first-time player to finish joining, in ticks.
     */
    private const val FIRST_LOGIN_DELAY = 20L

    fun register(luckPerms: LuckPerms, ecoPlugin: EcoPlugin) {
        val bus = luckPerms.eventBus

        bus.subscribe(ecoPlugin, UserPromoteEvent::class.java) { event ->
            dispatchForUser(event.user, event, TriggerLpPromote, text = event.groupTo.orElse(null))
        }

        bus.subscribe(ecoPlugin, UserDemoteEvent::class.java) { event ->
            dispatchForUser(event.user, event, TriggerLpDemote, text = event.groupTo.orElse(null))
        }

        bus.subscribe(ecoPlugin, NodeAddEvent::class.java) { event ->
            handleNodeMutate(event, event.node, isAdd = true)
        }

        bus.subscribe(ecoPlugin, NodeRemoveEvent::class.java) { event ->
            handleNodeMutate(event, event.node, isAdd = false)
        }

        bus.subscribe(ecoPlugin, NodeClearEvent::class.java) { event ->
            val user = event.target as? User ?: return@subscribe
            dispatchForUser(user, event, TriggerLpNodeClear, value = event.nodes.size.toDouble())
        }

        bus.subscribe(ecoPlugin, UserLoadEvent::class.java) { event ->
            dispatchForUser(event.user, event, TriggerLpUserLoad, text = event.user.username)
        }

        /*
        LuckPerms fires this while the player is still logging in, so it has to wait
        for them to actually be online before the trigger can be dispatched.
         */
        bus.subscribe(ecoPlugin, UserFirstLoginEvent::class.java) { event ->
            plugin.scheduler.global().runLater(FIRST_LOGIN_DELAY) {
                dispatchForUuid(event.uniqueId, event, TriggerLpFirstLogin, text = event.username)
            }
        }

        bus.subscribe(ecoPlugin, ContextUpdateEvent::class.java) { event ->
            val player = event.getSubject(Player::class.java).orElse(null) ?: return@subscribe
            dispatchForUuid(player.uniqueId, event, TriggerLpContextUpdate)
        }

        bus.subscribe(ecoPlugin, CustomMessageReceiveEvent::class.java) { event ->
            dispatchGlobal(event, TriggerLpCustomMessage, text = event.payload)
        }

        bus.subscribe(ecoPlugin, GroupCreateEvent::class.java) { event ->
            dispatchGlobal(event, TriggerLpGroupCreate, text = event.group.name)
        }

        bus.subscribe(ecoPlugin, GroupDeleteEvent::class.java) { event ->
            dispatchGlobal(event, TriggerLpGroupDelete, text = event.groupName)
        }

        bus.subscribe(ecoPlugin, TrackAddGroupEvent::class.java) { event ->
            dispatchGlobal(event, TriggerLpTrackAddGroup, text = event.group)
        }

        bus.subscribe(ecoPlugin, TrackRemoveGroupEvent::class.java) { event ->
            dispatchGlobal(event, TriggerLpTrackRemoveGroup, text = event.group)
        }

        bus.subscribe(ecoPlugin, ConfigReloadEvent::class.java) { event ->
            dispatchGlobal(event, TriggerLpConfigReload)
        }

        bus.subscribe(ecoPlugin, PostSyncEvent::class.java) { event ->
            dispatchGlobal(event, TriggerLpPostSync)
        }

        bus.subscribe(ecoPlugin, LogNotifyEvent::class.java) { event ->
            dispatchGlobal(event, TriggerLpLogNotify, text = event.entry.description)
        }

        /*
        Conditions read from the LuckPerms cache, so effects have to be recalculated
        whenever that cache changes. This is not exposed as a trigger, as it fires
        far too often to be useful as one.
         */
        bus.subscribe(ecoPlugin, UserDataRecalculateEvent::class.java) { event ->
            val player = Bukkit.getPlayer(event.user.uniqueId) ?: return@subscribe

            plugin.scheduler.on(player).run {
                player.toDispatcher().updateEffects()
            }
        }
    }

    private fun handleNodeMutate(event: NodeMutateEvent, node: Node, isAdd: Boolean) {
        val user = event.target as? User ?: return

        dispatchForUser(
            user,
            event,
            if (isAdd) TriggerLpNodeAdd else TriggerLpNodeRemove,
            text = node.key
        )

        when {
            NodeType.INHERITANCE.matches(node) -> dispatchForUser(
                user,
                event,
                if (isAdd) TriggerLpGroupAdd else TriggerLpGroupRemove,
                text = NodeType.INHERITANCE.cast(node).groupName
            )

            NodeType.PERMISSION.matches(node) -> dispatchForUser(
                user,
                event,
                if (isAdd) TriggerLpPermissionAdd else TriggerLpPermissionRemove,
                text = NodeType.PERMISSION.cast(node).permission
            )

            NodeType.META.matches(node) -> dispatchForUser(
                user,
                event,
                TriggerLpMetaChange,
                text = NodeType.META.cast(node).metaKey
            )

            NodeType.PREFIX.matches(node) -> {
                val prefix = NodeType.PREFIX.cast(node)
                dispatchForUser(user, event, TriggerLpPrefixChange, prefix.metaValue, prefix.priority.toDouble())
            }

            NodeType.SUFFIX.matches(node) -> {
                val suffix = NodeType.SUFFIX.cast(node)
                dispatchForUser(user, event, TriggerLpSuffixChange, suffix.metaValue, suffix.priority.toDouble())
            }
        }
    }

    private fun dispatchForUser(
        user: User,
        event: LuckPermsEvent,
        trigger: Trigger,
        text: String? = null,
        value: Double = 1.0
    ) = dispatchForUuid(user.uniqueId, event, trigger, text, value)

    private fun dispatchForUuid(
        uuid: UUID,
        event: LuckPermsEvent,
        trigger: Trigger,
        text: String? = null,
        value: Double = 1.0
    ) {
        val player = Bukkit.getPlayer(uuid) ?: return

        plugin.scheduler.on(player).run {
            trigger.dispatch(
                player.toDispatcher(),
                TriggerData(
                    player = player,
                    location = player.location,
                    event = LuckPermsEventWrapper(event),
                    text = text,
                    value = value
                )
            )
        }
    }

    private fun dispatchGlobal(
        event: LuckPermsEvent,
        trigger: Trigger,
        text: String? = null,
        value: Double = 1.0
    ) {
        plugin.scheduler.global().run {
            trigger.dispatch(
                GlobalDispatcher,
                TriggerData(
                    dispatcher = GlobalDispatcher,
                    event = LuckPermsEventWrapper(event),
                    text = text,
                    value = value
                )
            )
        }
    }
}
