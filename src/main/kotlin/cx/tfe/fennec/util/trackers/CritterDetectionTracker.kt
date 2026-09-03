package cx.tfe.fennec.util.trackers

import com.google.common.eventbus.Subscribe
import cx.tfe.fennec.Fennec
import cx.tfe.fennec.data.Critters
import cx.tfe.fennec.events.Event
import cx.tfe.fennec.util.Helpers.debug
import cx.tfe.fennec.util.Helpers.noControlCodes
import net.minecraft.world.entity.decoration.ArmorStand

/**
 * Estimates how many of each critter actually spawned in the current
 * Critter Safari instance, by counting the named armor stands that hover
 * over every critter (fox, head display, whatever the underlying entity
 * is — doesn't matter, we only ever look at the armor stand's name).
 *
 * A caught critter's armor stand despawns, so a plain "count visible right
 * now" would go DOWN every time you catch one — that's wrong, we want a
 * monotonically non-decreasing estimate of the total that spawned. So each
 * scan computes `caught + currently visible` (folding CritterTracker's
 * catch count back in to cover the ones that are no longer visible because
 * they're already caught) and only raises [detected] if that's bigger than
 * what's already stored — never lowers it.
 *
 * Only scans while actually inside a safari (SafariInstanceTracker), both
 * to avoid burning cycles elsewhere and to avoid false-positive name
 * collisions with unrelated armor stands outside the minigame.
 */
object CritterDetectionTracker {

    private val detected: MutableMap<Critters, Int> = mutableMapOf()

    private val nameMatchers: List<Pair<Critters, Regex>> =
        Critters.entries
            .sortedByDescending { it.gameName.length }
            .map { it to Regex("""\b${Regex.escape(it.gameName)}\b""") }

    // Entity counts don't need per-tick precision; scanning a few times a
    // second instead of all 20 saves some unnecessary work.
    private const val SCAN_INTERVAL_TICKS = 10
    private var ticksUntilScan = 0

    fun detectedOf(critter: Critters): Int = detected[critter] ?: 0

    /** Called by SafariInstanceTracker when a fresh instance is entered. */
    fun reset() {
        detected.clear()
    }

    @Subscribe
    fun onTick(event: Event.ClientTickEvent) {
        if (!SafariInstanceTracker.inSafariInstance) return

        if (ticksUntilScan > 0) {
            ticksUntilScan--
            return
        }
        ticksUntilScan = SCAN_INTERVAL_TICKS
        scan()
    }

    private fun scan() {
        debug("Scanning for critters...")

        val level = Fennec.mc.level ?: return

        val visibleCounts = mutableMapOf<Critters, Int>()
        for (entity in level.entitiesForRendering()) {
            if (entity !is ArmorStand) continue
            val name = entity.customName?.string?.noControlCodes() ?: continue
            val critter = nameMatchers.firstOrNull { (_, regex) -> regex.containsMatchIn(name) }?.first ?: continue
            visibleCounts[critter] = (visibleCounts[critter] ?: 0) + 1
        }

        for (critter in Critters.entries) {
            val visible = visibleCounts[critter] ?: 0
            val liveTotal = CritterTracker.countOf(critter) + visible
            if (liveTotal > detectedOf(critter)) {
                detected[critter] = liveTotal
            }
        }
    }
}