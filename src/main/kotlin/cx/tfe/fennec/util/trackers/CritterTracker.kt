package cx.tfe.fennec.util

import com.google.common.eventbus.Subscribe
import cx.tfe.fennec.data.Critters
import cx.tfe.fennec.events.Event
import cx.tfe.fennec.util.Helpers.debug
import cx.tfe.fennec.util.Helpers.noControlCodes
import net.minecraft.client.multiplayer.chat.GuiMessageSource

/**
 * Always-on background tracker for critter catches, parsed straight out of
 * chat. This is registered once in Fennec.onInitialize() — NOT through
 * Module.toggle() — so catches are tallied no matter what biome the player
 * is currently standing in, and even while SafariOverlay itself is toggled
 * off. SafariOverlay just reads [countOf] when it draws.
 *
 * Both of the following count as a "catch" here, since either one means you
 * ended up with a shard for that critter:
 *   CAPTURE! You caught a Foxtrot and gained 2x Foxtrot Shard!
 *   LOOT SHARE! You recieved a Hideonwall Shard from OtherPlayer123 catching a Hideonwall!
 *
 */
object CritterTracker {

    private val byGameName: Map<String, Critters> = Critters.entries.associateBy { it.gameName }

    private val counts: MutableMap<Critters, Int> = mutableMapOf()

    private const val QUANTITY = """(?:\d+x|a)"""

    private val CAPTURE_REGEX =
        Regex("""CAPTURE! You caught a (.+?) (?:and )?gained $QUANTITY .+? Shard!""")

    private val CAPTURE_REGEX_ALT =
        Regex("""CAPTURE! You found the (.+?), and as a reward it gave you $QUANTITY .+? Shard!""")

    // "LOOT SHARE! You received 2x Hideonwall Shard from OtherPlayer123 catching a Hideonwall!"
    private val LOOT_SHARE_REGEX =
        Regex("""LOOT SHARE! You received $QUANTITY .+? Shard from .+? catching a (.+?)!""")

    fun countOf(critter: Critters): Int = counts[critter] ?: 0

    fun totalFor(critters: Collection<Critters>): Int = critters.sumOf { countOf(it) }

    /** Clears all catch counts. Called by SafariInstanceTracker on entering a fresh instance. */
    fun reset() {
        counts.clear()
    }

    @Subscribe
    fun onChatMessage(event: Event.OnChatMessage) {
        if (event.source != GuiMessageSource.SYSTEM_SERVER) return

        val text = event.message.string.noControlCodes().replace(Regex("""\s+"""), " ")

        if (text.contains("LOOT SHARE") || text.contains("CAPTURE")) {
            debug("Detected critter message, matching regex now!")
        }

        CAPTURE_REGEX.findAll(text).forEach { recordCatch(it.groupValues[1]) }
        CAPTURE_REGEX_ALT.findAll(text).forEach { recordCatch(it.groupValues[1]) }
        LOOT_SHARE_REGEX.findAll(text).forEach { recordCatch(it.groupValues[1]) }
    }

    private fun recordCatch(gameName: String) {
        debug("Caught '$gameName'")

        val critter = byGameName[gameName] ?: return
        counts[critter] = (counts[critter] ?: 0) + 1
    }
}