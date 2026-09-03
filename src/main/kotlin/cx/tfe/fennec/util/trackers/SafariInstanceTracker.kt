package cx.tfe.fennec.util.trackers

import com.google.common.eventbus.Subscribe
import cx.tfe.fennec.events.Event
import cx.tfe.fennec.util.Helpers.debug
import cx.tfe.fennec.util.Helpers.noControlCodes
import net.minecraft.client.multiplayer.chat.GuiMessageSource

/**
 * Detects whether the player is currently inside a Critter Safari instance.
 */
object SafariInstanceTracker {

    var inSafariInstance: Boolean = false
        private set
    var primed: Boolean = false
        private set
    var ticksToWait = 0
        private set

    private val ENTER_CRITTER_SAFARI_REGEX = Regex("""(?:\[[^]]+]\s)?\S+ entered Critter Safari!""")

    @Subscribe
    fun onTick(event: Event.ClientTickEvent) {
        if (ticksToWait == 1)
            debug("Critter Safari instance tracker timed out while waiting for server swap!")
        if (ticksToWait > 0)
            ticksToWait -= 1
        if (ticksToWait <= 0)
            primed = false
    }

    @Subscribe
    fun onChatMessage(event: Event.OnChatMessage) {
        if (event.source != GuiMessageSource.SYSTEM_SERVER) return

        val text = event.message.string.noControlCodes()
        if (ENTER_CRITTER_SAFARI_REGEX.containsMatchIn(text)) {
            primed = true
            ticksToWait += 200
            debug("Critter Safari regex primed instance tracker (SOURCE: ${event.source.name})")
        }
    }

    @Subscribe
    fun onServerSwitch(event: Event.ServerSwitchEvent) {
        if (primed) {
            primed = false
            inSafariInstance = true
            CritterTracker.reset()
            CritterDetectionTracker.reset()
            ticksToWait = 0
            debug("Entered Critter Safari.")
        } else if (inSafariInstance) {
            inSafariInstance = false
            debug("Left Critter Safari.")
        }
    }
}