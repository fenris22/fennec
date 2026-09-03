package cx.tfe.fennec.features.impl.debug

import cx.tfe.fennec.config.ConfigManager
import cx.tfe.fennec.features.Category
import cx.tfe.fennec.features.Module

/**
 * While enabled, every loaded LivingEntity (mobs, animals, other players)
 * renders with the vanilla glowing outline — the same visual the Glowing
 * status effect produces.
 *
 * There's no tick logic or event subscription here on purpose: the actual
 * outlining is done by EntityGlowMixin, which overrides
 * Entity#isCurrentlyGlowing() (the exact method the renderer already
 * checks) to return true whenever HighlightMobs.enabled is true. No entity
 * state is touched, nothing needs to be reset when this is toggled off —
 * it just stops overriding the return value and everything goes back to
 * however it normally would've rendered.
 */
object ShowEntities : Module("highlightMobs", "Highlight Mobs", Category.DEBUG) {

    override fun onEnable() {
        ConfigManager.config.highlightMobs = true
        ConfigManager.save()
    }

    override fun onDisable() {
        ConfigManager.config.highlightMobs = false
        ConfigManager.save()
    }

}