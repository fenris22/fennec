package cx.tfe.fennec.features.impl

import com.google.common.eventbus.Subscribe
import cx.tfe.fennec.events.Event
import cx.tfe.fennec.features.Category
import cx.tfe.fennec.features.Module
import cx.tfe.fennec.util.Helpers.debug

object Debug: Module("debug","Debug", Category.DEBUG) {

    @Subscribe
    fun onServerSwitch(event: Event.ServerSwitchEvent) {
        if (enabled) {
            debug("Server Switch Event Triggered")
        }
    }

}