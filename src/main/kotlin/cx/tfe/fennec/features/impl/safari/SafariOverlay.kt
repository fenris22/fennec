package cx.tfe.fennec.features.impl.safari

import cx.tfe.fennec.Fennec
import cx.tfe.fennec.config.ConfigManager
import cx.tfe.fennec.data.Biomes
import cx.tfe.fennec.data.Critters
import cx.tfe.fennec.features.Category
import cx.tfe.fennec.features.Module
import cx.tfe.fennec.util.trackers.BiomeTracker
import cx.tfe.fennec.util.trackers.CritterDetectionTracker
import cx.tfe.fennec.util.CritterTracker
import cx.tfe.fennec.util.trackers.SafariInstanceTracker
import net.minecraft.client.DeltaTracker
import net.minecraft.client.gui.GuiGraphicsExtractor

/**
 * Draws a simple overlay listing the 4 safari biome categories. Whichever
 * biome box (see [Biomes]) the player is currently standing in expands to
 * list that biome's critters, showing catch progress against the live
 * detected count from [CritterDetectionTracker], plus the static expected
 * range from [Critters.instanceAmount] for reference.
 */
object SafariOverlay : Module("safariOverlay","Safari Overlay", Category.SAFARI) {

    private const val PADDING = 4
    private const val LINE_HEIGHT = 10
    private const val START_X = 6
    private const val START_Y = 6

    private const val BG_COLOR = 0x90000000.toInt()
    private const val HEADER_COLOR = 0xFFFFFFFF.toInt()
    private const val ACTIVE_HEADER_COLOR = 0xFFFFD966.toInt()
    private const val LINE_BASE_COLOR = 0xFFFFFFFF.toInt()

    override fun onEnable() {
        ConfigManager.config.safariOverlayEnabled = true
        ConfigManager.save()
    }

    override fun onDisable() {
        ConfigManager.config.safariOverlayEnabled = false
        ConfigManager.save()
    }

    fun render(graphics: GuiGraphicsExtractor, delta: DeltaTracker) {
        if (!SafariInstanceTracker.inSafariInstance) return

        val font = Fennec.mc.font
        val currentBiome = BiomeTracker.currentBiome

        // Build the lines up front so we can measure the box before drawing it.
        data class Line(val text: String, val color: Int)
        val lines = mutableListOf<Line>()

        for (biome in Biomes.entries) {
            val isActive = biome == currentBiome
            val biomeCritters = Critters.entries.filter { it.biome == biome }
            val biomeTotal = CritterTracker.totalFor(biomeCritters)

            lines.add(
                Line(
                    "${biome.game_name} (${biomeTotal})",
                    if (isActive) ACTIVE_HEADER_COLOR else HEADER_COLOR
                )
            )

            if (isActive) {
                biomeCritters.forEach { critter ->
                    lines.add(Line(formatCritterLine(critter), LINE_BASE_COLOR))
                }
            }
        }

        if (lines.isEmpty()) return

        val boxWidth = lines.maxOf { font.width(it.text) } + PADDING * 2
        val boxHeight = lines.size * LINE_HEIGHT + PADDING * 2

        graphics.fill(
            START_X - PADDING,
            START_Y - PADDING,
            START_X - PADDING + boxWidth,
            START_Y - PADDING + boxHeight,
            BG_COLOR
        )

        var y = START_Y
        for ((text, color) in lines) {
            graphics.text(font, text, START_X, y, color, true)
            y += LINE_HEIGHT
        }
    }

    /**
     * Builds e.g. "  §eFoxtrot §f0/5 §7(6-8)": critter name in yellow,
     * caught/detected in white, the static instanceAmount range in grey.
     * §a/§e/§f/§7/§r are vanilla legacy formatting codes — same mechanism
     * as before, GuiGraphicsExtractor's plain-string text() processes them
     * directly, no Component wrapping needed.
     *
     * Note: unlike the previous version, this doesn't special-case 0..0 or
     * min==max instanceAmount ranges — every critter gets the same
     * "caught/detected (min-max)" shape now, including e.g. "(0-0)" or
     * "(1-1)".
     */
    private fun formatCritterLine(critter: Critters): String {
        val caught = CritterTracker.countOf(critter)
        val detectedCount = CritterDetectionTracker.detectedOf(critter)
        val min = critter.instanceAmount.first
        val max = critter.instanceAmount.last

        if (min == max) {
            if (min == 0) {
                return "  §e${critter.gameName} §f$caught"
            }
            return "  §e${critter.gameName} §f$caught/$min §3($min)"
        }
        return "  §e${critter.gameName} §f$caught/$detectedCount §3($min-$max)"
    }
}