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

    // The shard quantity can read either "a" (singular, one shard) or
    // "Nx" (e.g. "2x", "3x" for a multiplied reward) — this token is
    // reused across all three regexes below instead of hardcoding \d+x in
    // each, since all three make the same assumption about quantity.
    private const val QUANTITY = """(?:\d+x|a)"""

    // "CAPTURE! You caught a Foxtrot and gained 2x Foxtrot Shard!"
    // "CAPTURE! You caught a Foxtrot gained a Foxtrot Shard!" (no "and", singular)
    // No ^/$ anchors: when multiple catches land in the same tick they can
    // arrive as one \n-joined chat message rather than separate addMessage
    // calls, and anchoring to the whole string would reject that entirely.
    // `.` doesn't match newlines by default, so this still can't accidentally
    // span across lines.
    private val CAPTURE_REGEX =
        Regex("""CAPTURE! You caught a (.+?) (?:and )?gained $QUANTITY .+? Shard!""")

    private val CAPTURE_REGEX_ALT =
        Regex("""CAPTURE! You found the (.+?), and as a reward it gave you $QUANTITY .+? Shard!""")

    // "LOOT SHARE! You received a Hideonwall Shard from OtherPlayer123 catching a Hideonwall!"
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

        // Chat Components come through with §-based formatting codes attached
        // (colors, bold, etc). Strip them first or these regexes will never
        // match — this is exactly what Helpers.noControlCodes() is for.
        //
        // Also collapse runs of whitespace to a single space: messages have
        // shown up with doubled-up spaces (e.g. "caught a  Foxtrot"), which
        // would otherwise fail every regex below since they only expect a
        // single space between tokens.
        val text = event.message.string.noControlCodes().replace(Regex("""\s+"""), " ")

        if (text.contains("LOOT SHARE") || text.contains("CAPTURE")) {
            debug("Detected critter message, matching regex now!")
        }

        // findAll, not find(): a single chat message can contain more than
        // one catch line (multiple critters caught the same tick, or a
        // capture and a loot share bundled together), and every one of them
        // needs to be recorded, not just the first match in the string.
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