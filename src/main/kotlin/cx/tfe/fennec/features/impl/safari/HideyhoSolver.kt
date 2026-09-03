package cx.tfe.fennec.features.impl

import com.google.common.eventbus.Subscribe
import cx.tfe.fennec.Fennec
import cx.tfe.fennec.config.ConfigManager
import cx.tfe.fennec.data.Critters
import cx.tfe.fennec.events.Event
import cx.tfe.fennec.features.Category
import cx.tfe.fennec.features.Module
import cx.tfe.fennec.util.CritterTracker
import cx.tfe.fennec.util.Helpers.debug
import cx.tfe.fennec.util.Helpers.noControlCodes
import cx.tfe.fennec.util.Helpers.sendCommand
import cx.tfe.fennec.util.Helpers.tell
import cx.tfe.fennec.util.trackers.SafariInstanceTracker
import net.minecraft.client.multiplayer.chat.GuiMessageSource
import net.minecraft.world.entity.decoration.ArmorStand
import kotlin.to

object HideyhoSolver: Module("hideyhoSolver","Hideyho Solver", Category.SAFARI) {

    // Entity counts don't need per-tick precision; scanning a few times a
    // second instead of all 20 saves some unnecessary work.
    private const val SCAN_INTERVAL_TICKS = 10
    private var ticksUntilScan = 0

    var detected: Boolean = false
    var talkedToHideyho = false

    /** Called by SafariInstanceTracker when a fresh instance is entered. */
    fun reset() {
        detected = false
        talkedToHideyho = false
    }

    override fun onEnable() {
        ConfigManager.config.hideyhoSolver = true
        ConfigManager.save()
    }

    override fun onDisable() {
        ConfigManager.config.hideyhoSolver = false
        ConfigManager.save()
    }

    @Subscribe
    fun onChatMessage(event: Event.OnChatMessage) {
        if (event.source != GuiMessageSource.SYSTEM_SERVER) return

        val message = event.message.string.noControlCodes()
        if (message.contains("[MOB] Hideyho: No peeking!")) {
            talkedToHideyho = true
            tell("Waiting for Hideyho to hide from us...")
            ticksUntilScan = 100
        }
    }

    @Subscribe
    fun onTick(event: Event.ClientTickEvent) {
        if (!SafariInstanceTracker.inSafariInstance) return
        if (!enabled) return
        if (detected) return
        if (!talkedToHideyho) return

        if (ticksUntilScan > 0) {
            ticksUntilScan--
            return
        }
        ticksUntilScan = SCAN_INTERVAL_TICKS
        scan()
    }

    private fun scan() {
        debug("Scanning for hideyho...")

        val level = Fennec.mc.level ?: return

        for (entity in level.entitiesForRendering()) {
            if (entity !is ArmorStand) continue
            val name = entity.customName?.string?.noControlCodes() ?: continue
            if (name.contains("Hideyho")) {
                detected = true
                sendCommand("shnav ${entity.x} ${entity.y} ${entity.z}")
            }
        }
    }

}