package cx.tfe.fennec.util.trackers

import com.google.common.eventbus.Subscribe
import cx.tfe.fennec.Fennec
import cx.tfe.fennec.data.Biomes
import cx.tfe.fennec.events.Event
import cx.tfe.fennec.events.EventBus

/**
 * Always-on tracker for which safari biome box (see [Biomes]) the player is
 * currently standing in.
 *
 * This used to live as an onTick @Subscribe method directly on the
 * SafariOverlay module, registered/unregistered via Module.toggle(). That
 * doesn't work anymore now that the overlay needs to be visible "regardless
 * of whether it's enabled" (per your last message) — EventBus.unregister()
 * removes *every* registration for a given instance, so if this were both
 * permanently registered here AND re-registered whenever someone toggles
 * the module on, toggling it off once would wipe both registrations and
 * silently kill tracking for good. Pulling it out into its own always-on
 * object, registered exactly once in Fennec.onInitialize(), avoids that.
 */
object BiomeTracker {

    var currentBiome: Biomes? = null
        private set

    @Subscribe
    fun onTick(event: Event.ClientTickEvent) {
        val player = Fennec.mc.player ?: return
        val pos = player.position()

        val region = Biomes.entries.firstOrNull { it.box.contains(pos) }
        if (region == currentBiome) return

        val previous = currentBiome
        currentBiome = region

        if (previous != null) {
            EventBus.post(Event.RegionExitEvent(previous))
        }
        if (region != null) {
            EventBus.post(Event.RegionEnterEvent(region))
        }
    }
}