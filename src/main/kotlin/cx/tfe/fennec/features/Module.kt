package cx.tfe.fennec.features

import cx.tfe.fennec.events.EventBus

abstract class Module(
    val name: String,
    val stageName: String,
    val category: Category
) {
    var enabled: Boolean = false
        private set

    fun toggle() {
        enabled = !enabled
        if (enabled) {
            EventBus.register(this)
            onEnable()
        } else {
            EventBus.unregister(this)
            onDisable()
        }
    }

    open fun onEnable() {}
    open fun onDisable() {}
}