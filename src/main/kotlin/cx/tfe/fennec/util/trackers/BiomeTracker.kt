package cx.tfe.fennec.util.trackers

import com.google.common.eventbus.Subscribe
import cx.tfe.fennec.Fennec
import cx.tfe.fennec.data.Biomes
import cx.tfe.fennec.events.Event
import cx.tfe.fennec.events.EventBus

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